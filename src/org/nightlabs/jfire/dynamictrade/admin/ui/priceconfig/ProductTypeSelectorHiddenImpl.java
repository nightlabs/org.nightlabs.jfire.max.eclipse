package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.jfire.accounting.gridpriceconfig.IFormulaPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.IResultPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPriceConfig;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.ProductTypeSelector;

public class ProductTypeSelectorHiddenImpl
		implements ProductTypeSelector
{
	private ArrayList<Item> productTypeItemList = new ArrayList<Item>();
	private Item packageProductTypeItem = null;

	@Override
	public ProductTypeSelector.Item getSelectedProductTypeItem(boolean throwExceptionIfNothingSelected)
	{
		if (productTypeItemList.isEmpty()) {
			if (throwExceptionIfNothingSelected)
				throw new IllegalStateException("Nothing selected, because productTypeItemList is empty!"); //$NON-NLS-1$

			return null;
		}
		return productTypeItemList.get(0);
	}

	@Override
	public IFormulaPriceConfig getSelectedProductType_FormulaPriceConfig(boolean throwExceptionIfNotPossible)
	{
		Item item = this.getSelectedProductTypeItem(throwExceptionIfNotPossible);
		if (item == null)
			return null;

		ProductType pt = item.getProductType();
		if (pt.isPackageOuter() && !item.isInnerVirtual()) {
			if (throwExceptionIfNotPossible)
				throw new IllegalStateException("ProductType \""+pt.getPrimaryKey()+"\" is a package and can therefore not provide a FormulaPriceConfig."); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}

		IPriceConfig priceConfig = pt.getInnerPriceConfig();
		if (priceConfig instanceof IFormulaPriceConfig)
			return (IFormulaPriceConfig)priceConfig;

		if (throwExceptionIfNotPossible) {
			if (priceConfig == null)
				throw new IllegalStateException("ProductType \""+pt.getPrimaryKey()+"\" has no PriceConfig assigned!"); //$NON-NLS-1$ //$NON-NLS-2$

			throw new IllegalStateException("ProductType \""+pt.getPrimaryKey()+"\" has a PriceConfig assigned which does NOT implement IFormulaPriceConfig!"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return null;
	}

	@Override
	public IResultPriceConfig getSelectedProductType_ResultPriceConfig(boolean throwExceptionIfNotPossible)
	{
		Item item = this.getSelectedProductTypeItem(throwExceptionIfNotPossible);
		if (item == null)
			return null;

		ProductType pt = item.getProductType();
		IPriceConfig priceConfig;

		if (pt.isPackageOuter() && !item.isInnerVirtual()) {
			priceConfig = pt.getPackagePriceConfig();
		}
		else {
			priceConfig = pt.getInnerPriceConfig();
		}

		IFormulaPriceConfig formulaPriceConfig = null;
		IResultPriceConfig stablePriceConfig = null;
		if (priceConfig instanceof IFormulaPriceConfig)
			formulaPriceConfig = (IFormulaPriceConfig)priceConfig;
		else if (priceConfig instanceof IResultPriceConfig)
			stablePriceConfig = (IResultPriceConfig)priceConfig;
		else {
			if (throwExceptionIfNotPossible && priceConfig == null)
				throw new IllegalStateException("ProductType \""+pt.getPrimaryKey()+"\" has no PriceConfig assigned!"); //$NON-NLS-1$ //$NON-NLS-2$

			if (throwExceptionIfNotPossible)
				throw new IllegalStateException("ProductType \""+pt.getPrimaryKey()+"\" has a PriceConfig assigned which is neither an instance of FormulaPriceConfig nor StablePriceConfig!"); //$NON-NLS-1$ //$NON-NLS-2$

			return null;
		}

		if (formulaPriceConfig != null) {
			stablePriceConfig = (IResultPriceConfig) formulaPriceConfig.getPackagingResultPriceConfig(
					pt.getPrimaryKey(),
					packageProductType.getPrimaryKey(),
					true);
		}

		return stablePriceConfig;
	}

	private ProductType packageProductType;
	@Override
	public ProductType getPackageProductType()
	{
		return packageProductType;
	}

	@Override
	public void setPackageProductType(ProductType packageProductType)
	{
		this.packageProductType = packageProductType;
		this.packageProductTypeItem = null;
		productTypeItemList.clear();

		if (packageProductType != null) {
			if (packageProductType.getInnerPriceConfig() != null) {
				productTypeItemList.add(new Item(false, true, packageProductType));
			}

			for (NestedProductTypeLocal nestedProductTypeLocal : packageProductType.getProductTypeLocal().getNestedProductTypeLocals()) {
				ProductType productType = nestedProductTypeLocal.getInnerProductTypeLocal().getProductType();

				productTypeItemList.add(new Item(false, false, productType));
			}

			if (packageProductType.getPackagePriceConfig() != null) {
				packageProductTypeItem = new Item(true, false, packageProductType);
				productTypeItemList.add(packageProductTypeItem);
			}

		} // if (assemblyPackageProductInfo != null) {

		fireSelectionChangedEvent();
	}

	private ListenerList selectionChangedListeners = new ListenerList();
	
	protected void fireSelectionChangedEvent()
	{
		SelectionChangedEvent e = new SelectionChangedEvent(this, getSelection());
		for (Object listener : selectionChangedListeners.getListeners()) {
			ISelectionChangedListener l = (ISelectionChangedListener)listener;
			l.selectionChanged(e);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return Returns an instance of <tt>StructuredSelection</tt> which is either empty
	 *   or contains the selected instance of <tt>ProductInfo</tt>.
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection()
	{
		if (productTypeItemList.isEmpty())
			return new StructuredSelection();

		return new StructuredSelection(productTypeItemList.get(0));
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection)
	{
		// we silently ignore it
	}

	@Override
	public Collection<Item> getProductTypeItems()
	{
		return productTypeItemList;
	}
}
