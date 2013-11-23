package org.nightlabs.jfire.trade.ui.detail;

import org.nightlabs.jfire.store.ProductType;


/**
 * This Factory creates {@link IProductTypeDetailView}s for a certain kind of {@link ProductType}
 * and can be registered via the extension-point org.nightlabs.jfire.productTypeDetailView
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface ProductTypeDetailViewFactory
{
	/**
	 * returns the {@link IProductTypeDetailView} to diplay the details for the {@link ProductType}
	 * defined by getProductTypeClass()
	 * 
	 * @return the {@link IProductTypeDetailView} to diplay the details for the {@link ProductType}
	 * defined by getProductTypeClass()
	 */
	IProductTypeDetailView createProductTypeDetailView();
	
	/**
	 * returns the Class which represents the kind of {@link ProductType} for which
	 * details should be displayed
	 * 
	 * @return the Class which represents the kind of {@link ProductType} for which
	 * details should be displayed
	 */
	Class<? extends ProductType> getProductTypeClass();
}
