package org.nightlabs.jfire.trade.ui.detail;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.jfire.store.ProductType;

/**
 * A Registry for {@link ProductTypeDetailViewFactory}s which can be
 * registered via the extension org.nightlabs.jfire.productTypeDetailView
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeDetailViewRegistry
extends AbstractEPProcessor
{
	private static ProductTypeDetailViewRegistry sharedInstance;

	public static ProductTypeDetailViewRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ProductTypeDetailViewRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new ProductTypeDetailViewRegistry();
				}
			}
		}
		return sharedInstance;
	}

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.ui.productTypeDetailView";	 //$NON-NLS-1$
	public static final String ELEMENT_PRODUCT_TYPE_DETAIL_VIEW = "productTypeDetailView"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PRODUCT_TYPE_DETAIL_VIEW_FACTORY = "productTypeDetailViewFactory";	 //$NON-NLS-1$

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		if (element.getName().equals(ELEMENT_PRODUCT_TYPE_DETAIL_VIEW))
		{
			String factoryClassName = element.getAttribute(ATTRIBUTE_PRODUCT_TYPE_DETAIL_VIEW_FACTORY);
			if (checkString(factoryClassName)) {
				try {
					ProductTypeDetailViewFactory factory = (ProductTypeDetailViewFactory) element.createExecutableExtension(ATTRIBUTE_PRODUCT_TYPE_DETAIL_VIEW_FACTORY);
					productTypeClass2DetailViewFactory.put(factory.getProductTypeClass(), factory);
				} catch (CoreException e) {
					throw new EPProcessorException("Could not create ProuctTypeDetailViewFactory class "+factoryClassName, e); //$NON-NLS-1$
				}
			}
		}
	}

	private Map<Class<? extends ProductType>, ProductTypeDetailViewFactory> productTypeClass2DetailViewFactory =
		new HashMap<Class<? extends ProductType>, ProductTypeDetailViewFactory>();

	private ProductTypeDetailViewFactory getProductTypeDetailViewFactory(Class<?> productTypeClass)
	{
		Class<?> clazz = productTypeClass;
		while (clazz != null) {
			ProductTypeDetailViewFactory factory = productTypeClass2DetailViewFactory.get(clazz);
			if (factory != null)
				return factory;

			for (Class<?> iface : clazz.getInterfaces()) {
				factory = getProductTypeDetailViewFactory(iface);
				if (factory != null)
					return factory;
			}

			clazz = clazz.getSuperclass();
		}
		return null;
	}

	public IProductTypeDetailView createProductTypeDetailView(Class<? extends ProductType> productTypeClass)
	{
		if (!isProcessed())
			checkProcessing();

//		ProductTypeDetailViewFactory factory = productTypeClass2DetailViewFactory.get(productTypeClass);
		ProductTypeDetailViewFactory factory = getProductTypeDetailViewFactory(productTypeClass);
		if (factory != null) {
			IProductTypeDetailView productTypeDetailView = factory.createProductTypeDetailView();
			if (productTypeDetailView != null)
				return productTypeDetailView;
			else
				return new GenericProductTypeDetailView();
		}
		return new GenericProductTypeDetailView();
	}
}
