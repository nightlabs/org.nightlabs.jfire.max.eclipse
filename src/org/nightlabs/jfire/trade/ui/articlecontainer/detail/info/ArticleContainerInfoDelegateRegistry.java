package org.nightlabs.jfire.trade.ui.articlecontainer.detail.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.util.reflect.ReflectUtil;

/**
 * Registry which processes the extension-point with the following extension-point id: {@link #EXTENSION_POINT_ID}.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerInfoDelegateRegistry extends AbstractEPProcessor
{
	public static final String FACTORY_ELEMENT_NAME = "articleContainerInfoDelegateFactory";
	public static final String EXTENSION_POINT_ID = TradePlugin.getDefault().getBundle().getSymbolicName() + "." + FACTORY_ELEMENT_NAME; //$NON-NLS-1$

	public static final Integer DEFAULT_INDEX_HINT = 50;

	private static ArticleContainerInfoDelegateRegistry sharedInstance;

	/**
	 * Returns and lazily creates a static instance of ArticleContainerInfoDelegateRegistry
	 */
	public static ArticleContainerInfoDelegateRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ArticleContainerInfoDelegateRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new ArticleContainerInfoDelegateRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
	}

	private Map<Class<? extends ArticleContainer>, SortedMap<Integer, ArticleContainerInfoDelegateFactory>> acClass2Factories;

	public ArticleContainerInfoDelegateRegistry() {
		acClass2Factories = new HashMap<Class<? extends ArticleContainer>, SortedMap<Integer, ArticleContainerInfoDelegateFactory>>();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		if (element.getName().equals(FACTORY_ELEMENT_NAME))
		{
			ArticleContainerInfoDelegateFactory factory = (ArticleContainerInfoDelegateFactory) element.createExecutableExtension("class");
			String indexHint = element.getAttribute("indexHint"); //$NON-NLS-1$
			Integer index = DEFAULT_INDEX_HINT;
			try {
				index = Integer.valueOf(indexHint);
			} catch (NumberFormatException e) {
				// do nothing but keep default
			}
			Class<? extends ArticleContainer> acClass = factory.getArticleContainerClass();
			SortedMap<Integer, ArticleContainerInfoDelegateFactory> index2Factory = acClass2Factories.get(acClass);
			if (index2Factory == null) {
				index2Factory = new TreeMap<Integer, ArticleContainerInfoDelegateFactory>();
				acClass2Factories.put(acClass, index2Factory);
			}
			index2Factory.put(index, factory);
		}
	}

	public ArticleContainerInfoDelegateFactory getArticleContainerInfoDelegateFactory(Class<? extends ArticleContainer> acClass)
	{
		ArticleContainerInfoDelegateFactory factory = getFactory(acClass);
		if (factory == null) {
			List<Class<?>> classes = ReflectUtil.collectTypeHierarchy(acClass);
			for (Class<?> clazz : classes) {
				factory = getFactory(clazz);
				if (factory != null) {
					return factory;
				}
			}
		}
		return factory;
	}

	protected ArticleContainerInfoDelegateFactory getFactory(Class clazz)
	{
		SortedMap<Integer, ArticleContainerInfoDelegateFactory> index2Factory = acClass2Factories.get(clazz);
		if (index2Factory != null) {
			return index2Factory.get(index2Factory.firstKey());
		}
		return null;
	}
}
