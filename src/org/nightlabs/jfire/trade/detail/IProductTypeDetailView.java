package org.nightlabs.jfire.trade.detail;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * Implementations of this interface provide GUI (a {@link Composite})
 * to display details for the given {@link ProductTypeID}
 *  
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface IProductTypeDetailView
{
	/**
	 * sets the {@link ProductTypeID} of the Product to display
	 * @param productTypeID the {@link ProductTypeID} of the Product to display
	 */
	void setProductTypeID(ProductTypeID productTypeID);
	
	/**
	 * returns the composite to display the the details for the productType
	 * 
	 * @param parent the parent Composite
	 * @return the composite to display the the details for the productType
	 */
	Composite createComposite(Composite parent);
}
