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

package org.nightlabs.jfire.trade.admin.gridpriceconfig;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPackagePriceConfig;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ProductTypeSelector extends ISelectionProvider
{
	static class Item {
		private boolean _package;
		private boolean _innerVirtual;
		private ProductType productType;

		public Item(boolean isPackage, boolean isInnerVirtual, ProductType productType) {
			this._package = isPackage;
			this._innerVirtual = isInnerVirtual;
			this.productType = productType;
		}
		/**
		 * @return Returns the productType.
		 */
		public ProductType getProductType()
		{
			return productType;
		}
		/**
		 * @return Returns the _package.
		 */
		public boolean isPackage()
		{
			return _package;
		}
		/**
		 * @return Returns the _innerVirtual.
		 */
		public boolean isInnerVirtual()
		{
			return _innerVirtual;
		}

		public GridPriceConfig getPriceConfig()
		{
			if (productType.isPackageOuter() && !isInnerVirtual())
				return (GridPriceConfig) productType.getPackagePriceConfig();

			return (GridPriceConfig) productType.getInnerPriceConfig();
		}
		public void setPriceConfig(GridPriceConfig priceConfig)
		{
			if (productType.isPackageOuter() && !isInnerVirtual())
				productType.setPackagePriceConfig((IPackagePriceConfig) priceConfig);
			else
				productType.setInnerPriceConfig((IInnerPriceConfig) priceConfig);
		}
	}

	Item getSelectedProductTypeItem(boolean throwExceptionIfNothingSelected);
	
	void setPackageProductType(ProductType packageProductType);
	ProductType getPackageProductType();

//	Item getPackageProductTypeItem(boolean throwExceptionIfNull);

	/**
	 * @return Returns the <tt>FormulaPriceConfig</tt> which is assigned to
	 * the currently selected <tt>ProductType</tt>. If this does not have a <tt>FormulaPriceConfig</tt>
	 * assigned, returns <tt>null</tt> or throws an IllegalStateException (depending on <tt>throwExceptionIfNotPossible</tt>).
	 */
	org.nightlabs.jfire.accounting.gridpriceconfig.IFormulaPriceConfig getSelectedProductType_FormulaPriceConfig(boolean throwExceptionIfNotPossible);

	/**
	 * @return If the selected <tt>ProductInfo</tt> has a <tt>StablePriceConfig</tt> assigned,
	 * it is directly returned. If a <tt>FormulaPriceConfig</tt> is assigned, the appropriate
	 * <tt>StablePriceConfig</tt> (which stores the formula's results in the context of the current
	 * <tt>AssemblyPackageProductInfo</tt>) is resolved and returned. If neither a <tt>FormulaPriceConfig</tt>
	 * nor a <tt>StablePriceConfig</tt> is assigned, <tt>null</tt> will be returned or an
	 * <tt>IllegalStateException</tt> thrown - depending on the parameter <tt>throwExceptionIfNotPossible</tt>.
	 */
	org.nightlabs.jfire.accounting.gridpriceconfig.IResultPriceConfig getSelectedProductType_ResultPriceConfig(boolean throwExceptionIfNotPossible);

	/**
	 * @return Returns a <tt>Collection</tt> of {@link ProductTypeSelector.Item} with all <tt>ProductType</tt>s,
	 * including the virtual inner item and the item which points to the package (means the instance of the package
	 * <tt>ProductType</tt> is referenced from two items, once for the virtual inner and once for the package).
	 */
	Collection<Item> getProductTypeItems();
}
