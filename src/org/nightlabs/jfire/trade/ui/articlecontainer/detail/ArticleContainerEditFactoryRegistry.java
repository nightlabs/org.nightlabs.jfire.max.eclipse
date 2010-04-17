package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.util.reflect.ReflectUtil;

/**
 * This registry processes the extension-point <code>org.nightlabs.jfire.trade.ui.articleContainerEditFactory</code>
 * and can provide an {@link ArticleContainerEditFactory} for a particular type (class)
 * of {@link ArticleContainer}.
 * <p>
 * The registry applies inheritance resolving (including interfaces) while searching for a {@link ArticleContainerEditFactory}.
 * This means registrations on sub-classes of an {@link ArticleContainer} implementation will overwrite those.
 * </p> 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerEditFactoryRegistry
extends AbstractEPProcessor
{
	protected static final String FACTOY_ELEMENT_NAME = "articleContainerEditFactory"; //$NON-NLS-1$
	protected static final String ARTICLE_CONTAINER_ATTRIBUTE_NAME = "articleContainerClass"; //$NON-NLS-1$
	public static final String EXTENSION_POINT_ID = TradePlugin.getDefault().getBundle().getSymbolicName() + "." + FACTOY_ELEMENT_NAME; //$NON-NLS-1$
	
	protected static ArticleContainerEditFactoryRegistry _sharedInstance = null;

	private static boolean initializingSharedInstance = false;
	public static synchronized ArticleContainerEditFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new ArticleContainerEditFactoryRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}

	private Map<String, ArticleContainerEditFactory> articleContainerEditoFactories = new HashMap<String, ArticleContainerEditFactory>(); 
	
	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equals(FACTOY_ELEMENT_NAME)) {
			String articleContainerClass = element.getAttribute(ARTICLE_CONTAINER_ATTRIBUTE_NAME);
			ArticleContainerEditFactory factory = (ArticleContainerEditFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			articleContainerEditoFactories.put(articleContainerClass, factory);
		}
	}
	
	/**
	 * Searches the {@link ArticleContainerEditFactory} registered for the given articleContainerClass
	 * or one of its super-classes or super-interfaces.
	 * 
	 * @param articleContainerClass The class-name of the {@link ArticleContainer} to search an edit for.
	 * @return Either an {@link ArticleContainerEditFactory} or <code>null</code> if none could be found.
	 */
	public ArticleContainerEditFactory getArticleContainerEditFactory(String articleContainerClass) {
		Class<?> searchClass = null;
		try {
			searchClass = Class.forName(articleContainerClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not resolve ArticleContainer class: " + articleContainerClass, e); //$NON-NLS-1$
		}
		List<Class<?>> typeHierarchy = ReflectUtil.collectTypeHierarchy(searchClass);
		for (Class<?> classInHierarchy : typeHierarchy) {
			ArticleContainerEditFactory factory = (ArticleContainerEditFactory) articleContainerEditoFactories.get(classInHierarchy.getName());
			if (factory != null)
				return factory;
		}
		return null;
	}
}
