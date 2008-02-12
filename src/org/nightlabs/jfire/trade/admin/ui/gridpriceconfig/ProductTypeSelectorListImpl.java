/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.gridpriceconfig.IFormulaPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.IResultPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPriceConfig;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ProductTypeSelectorListImpl extends XComposite
		implements ProductTypeSelector, ISelectionProvider
{
	private Label productLabel;
	private List productTypeGUIList;
	private ArrayList<Item> productTypeItemList = new ArrayList<Item>();
	private Item packageProductTypeItem = null;

	/**
	 * @param parent
	 * @param style
	 */
	public ProductTypeSelectorListImpl(Composite parent, int style)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		productLabel = new Label(this, SWT.NONE);
		productLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.ProductTypeSelectorListImpl.productTypeLabel.text")); //$NON-NLS-1$
		productTypeGUIList = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData productListLayoutData = new GridData(GridData.FILL_BOTH);
		productTypeGUIList.setLayoutData(productListLayoutData);
		productTypeGUIList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				fireSelectionChangedEvent();
			}
		});
	}

	@Implement
	public ProductTypeSelector.Item getSelectedProductTypeItem(boolean throwExceptionIfNothingSelected)
	{
		int productIdx = productTypeGUIList.getSelectionIndex();
		if (throwExceptionIfNothingSelected && productIdx < 0)
			throw new IllegalStateException("Nothing selected!"); //$NON-NLS-1$
		
		if (productIdx < 0)
			return null;

		return productTypeItemList.get(productIdx);
	}

	@Implement
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

	@Implement
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

//	public Item getPackageProductTypeItem(boolean throwExceptionIfNull)
//	{
//		if (throwExceptionIfNull && packageProductTypeItem == null)
//			throw new NullPointerException("packageProductTypeItem");
//		return packageProductTypeItem;
//	}

	@Implement
	private ProductType packageProductType;
	public ProductType getPackageProductType()
	{
		return packageProductType;
	}

	@Implement
	public void setPackageProductType(ProductType packageProductType)
	{
		this.packageProductType = packageProductType;
		this.packageProductTypeItem = null;
		productTypeItemList.clear();
		productTypeGUIList.removeAll();

		if (packageProductType != null) {
			String languageID = Locale.getDefault().getLanguage();

			if (packageProductType.getInnerPriceConfig() != null) {
				productTypeItemList.add(new Item(false, true, packageProductType));
				productTypeGUIList.add(packageProductType.getName().getText(languageID) + Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.ProductTypeSelectorListImpl.packageProductType_base")); //$NON-NLS-1$
			}

//			EventInnerProductInfo assemblyInnerProductInfo = assemblyPackageProductInfo.getEventInnerProductInfo();
//			if (!(assemblyInnerProductInfo.getPriceConfig() instanceof FormulaPriceConfig))
//				throw new IllegalArgumentException("The PriceConfig of assemblyInnerProductInfo is not an instance of FormulaPriceConfig!");
//	
//			productTypeItemList.add(assemblyInnerProductInfo);
//			productTypeGUIList.add(assemblyInnerProductInfo.getPrimaryKey()); // TO DO i18n

			for (NestedProductTypeLocal nestedProductTypeLocal : packageProductType.getProductTypeLocal().getNestedProductTypeLocals()) {
				ProductType productType = nestedProductTypeLocal.getInnerProductTypeLocal().getProductType();
//				if (assemblyInnerProductInfo == productInfo)
//					continue;

				productTypeItemList.add(new Item(false, false, productType));
				productTypeGUIList.add(productType.getName().getText(languageID) + " [" + nestedProductTypeLocal.getQuantity() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (packageProductType.getPackagePriceConfig() != null) {
				packageProductTypeItem = new Item(true, false, packageProductType);
				productTypeItemList.add(packageProductTypeItem);
				productTypeGUIList.add(packageProductType.getName().getText(languageID) + Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.ProductTypeSelectorListImpl.packageProductType_total")); //$NON-NLS-1$
			}

			productTypeGUIList.select(0);
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

	@Implement
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
	@Implement
	public ISelection getSelection()
	{
		int selIdx = productTypeGUIList.getSelectionIndex();
		if (selIdx < 0)
			return new StructuredSelection(new Object[] { });

		return new StructuredSelection(productTypeItemList.get(selIdx));
	}

	@Implement
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	@Implement
	public void setSelection(ISelection selection)
	{
		if (selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) selection;
			if (sel.isEmpty())
				productTypeGUIList.setSelection(-1);
			else {
				ProductType spi = (ProductType) sel.getFirstElement();
				int idx = 0;
				for (Iterator it = productTypeItemList.iterator(); it.hasNext(); ++idx) {
					ProductType pi = (ProductType)it.next();
					if (spi.getPrimaryKey().equals(pi.getPrimaryKey())) {
						productTypeGUIList.setSelection(idx);
						break;
					}
				}
			}
		}
	}

	@Implement
	public Collection<Item> getProductTypeItems()
	{
		return productTypeItemList;
	}

}
