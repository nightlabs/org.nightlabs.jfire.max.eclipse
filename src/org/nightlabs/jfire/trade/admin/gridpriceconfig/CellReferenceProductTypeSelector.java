package org.nightlabs.jfire.trade.admin.gridpriceconfig;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

public interface CellReferenceProductTypeSelector
extends ISelectionProvider
{
	/**
	 * Get the {@link PriceConfigComposite} which has been previously set by {@link #setPriceConfigComposite(PriceConfigComposite)}.
	 *
	 * @return the {@link PriceConfigComposite} assigned by a prior call to {@link #setPriceConfigComposite(PriceConfigComposite)}
	 *		or <code>null</code> if the setter was not yet called.
	 */
	PriceConfigComposite getPriceConfigComposite();

	/**
	 * Set the {@link PriceConfigComposite}. This method is called by the framework once
	 * in order to give the implementation of <code>CellReferenceProductTypeSelector</code>
	 * the possibility to change its behaviour according to the data in the current price config.
	 *
	 * @param priceConfigComposite the {@link PriceConfigComposite} which makes use of the
	 *		insert-cell-reference-wizard.
	 */
	void setPriceConfigComposite(PriceConfigComposite priceConfigComposite);

	/**
	 * Create the UI element for selecting a <code>ProductType</code>.
	 *
	 * @param parent the container of the newly created composite.
	 * @return a new composite for selecting a <code>ProductType</code>.
	 */
	Composite createComposite(Composite parent);

	/**
	 * @return <code>null</code> or an instance of {@link ProductTypeID} referencing the currently selected {@link ProductType}.
	 */
	ProductTypeID getSelectedProductTypeID();

	/**
	 * Currently, the CellReferenceProductTypeSelector doesn't need to support this feature.
	 * This might change in the future. The default implementation in {@link AbstractCellReferenceProductTypeSelector}
	 * therefore throws an exception in this method.
	 */
	public void setSelection(ISelection selection);
}

