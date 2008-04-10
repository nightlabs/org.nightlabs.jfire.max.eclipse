package org.nightlabs.jfire.trade.ui.detail;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
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
	 * Called when the view is created with a memento
	 * where the view might have placed values in 
	 * order to restore its state.
	 * <p>
	 * Note that the memento might be empty or <code>null</code>
	 * </p>
	 * 
	 * @param memento The memento where this view might have stored values.
	 */
	void init(IMemento memento);
	
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
	
	/**
	 * Called when the this view is not longer needed and give it the 
	 * opportunity to store its state to the given memento in order 
	 * to restore it later.
	 * 
	 * @param memento The memento to store values to be able to restore later.
	 *                Note, that the memento will be for this view exclusively,
	 *                so there is no need to create sub-elements if not necessary.
	 */
	void saveState(IMemento memento);
}
