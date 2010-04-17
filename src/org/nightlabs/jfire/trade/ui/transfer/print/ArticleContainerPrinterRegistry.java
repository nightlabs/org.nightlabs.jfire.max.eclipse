package org.nightlabs.jfire.trade.ui.transfer.print;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.jfire.trade.ui.TradePlugin;

/**
 * Extension-Point Registry for registering {@link IArticleContainerPrinterFactory}s.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerPrinterRegistry
extends AbstractEPProcessor
{
	public static final String FACTOY_ELEMENT_NAME = "articleContainerPrinterFactory"; //$NON-NLS-1$
	public static final String EXTENSION_POINT_ID = TradePlugin.getDefault().getBundle().getSymbolicName() + "." + FACTOY_ELEMENT_NAME; //$NON-NLS-1$

	private static ArticleContainerPrinterRegistry sharedInstance;

	private List<IArticleContainerPrinterFactory> factories = new ArrayList<IArticleContainerPrinterFactory>();

	private ArticleContainerPrinterRegistry() {};

	/**
	 * Returns and lazily creates a static instance of ArticleContainerPrinterRegistry
	 */
	public static ArticleContainerPrinterRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ArticleContainerPrinterRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new ArticleContainerPrinterRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
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
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equals(FACTOY_ELEMENT_NAME)) {
			IArticleContainerPrinterFactory factory = (IArticleContainerPrinterFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			factories.add(factory);
		}
	}

	public List<IArticleContainerPrinterFactory> getFactories() {
		checkProcessing();
		return factories;
	}
}
