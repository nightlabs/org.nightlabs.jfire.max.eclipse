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

package org.nightlabs.jfire.trade.admin.ui.producttype;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public abstract class AbstractSaleAccessControlHelper implements SaleAccessControlHelper
{
	private ProductType productType;

//	@Implement
//	public Set<String> getFetchGroupsProductType()
//	{
//		Set<String> fetchGroups = new HashSet<String>();
//		fetchGroups.add(ProductType.FETCH_GROUP_INNER_PRICE_CONFIG);
//		fetchGroups.add(ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG);
//		fetchGroups.add(ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE);
//		return fetchGroups;
//	}

	@Override
	public void setProductType(ProductType productType)
	{
		// access all fields we'll access later so we know already now, whether fetch-groups were missing
		if (productType != null) {
			productType.getExtendedProductType();
			productType.getInnerPriceConfig();
			productType.getPackagePriceConfig();
			productType.getProductTypeLocal().getLocalAccountantDelegate();
		}

		// and assign
		this.productType = productType;
	}

	@Override
	public ProductType getProductType()
	{
		return productType;
	}

	private boolean hasPriceConfig(boolean silent)
	{
//		if (productType.getInnerPriceConfig() == null) {
		if (productType.getPriceConfigInPackage(productType.getPrimaryKey()) == null) {
			if (!silent) {
				MessageDialog.openError(
						RCPUtil.getActiveShell(),
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorNoPriceConfigDialog.title"), //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorNoPriceConfigDialog.message")); //$NON-NLS-1$
			}

			return false;
		}

		if (productType.isPackageOuter() && productType.getPackagePriceConfig() == null) {
			throw new IllegalStateException("The productType \""+productType.getPrimaryKey()+"\" is a package, but has no package-price-config assigned!"); //$NON-NLS-1$ //$NON-NLS-2$
//			MessageDialog.openError(
//					RCPUtil.getWorkbenchShell(),
//					"Package Without Package Price Configuration!",
//					"This is a package, but there is no package-price-config! It's likely that this is a bug.");
//
//			return false;
		}

		return true;
	}

	protected boolean hasLocalAccountantDelegate(boolean silent)
	{
		if (productType.getProductTypeLocal().getLocalAccountantDelegate() == null) {
			if (!silent) {
				MessageDialog.openError(
						RCPUtil.getActiveShell(),
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorNoMoneyFlowConfigDialog.title"), //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorNoMoneyFlowConfigDialog.message")); //$NON-NLS-1$
			}

			return false;
		}

		return true;
	}
	
	protected boolean isLeaf(boolean silent)
	{
		if (!productType.isInheritanceLeaf()) {
			if (!silent) {
				MessageDialog.openError(
						RCPUtil.getActiveShell(),
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorNotALeafDialog.title"), //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorNotALeafDialog.message")); //$NON-NLS-1$
			}

			return false;
		}

		return true;
	}
	
	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlHelper#canPublish(boolean)
	 */
	public boolean canPublish(boolean silent)
	{
		if (!productType.getExtendedProductType().isPublished()) {
			if (!silent) {
				MessageDialog.openError(
						RCPUtil.getActiveShell(),
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorParentNotPublishedDialog.title"), //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper.errorParentNotPublishedDialog.message")); //$NON-NLS-1$
			}

			return false;
		}
		return true;
	}

	public boolean canConfirm(boolean silent)
	{
		return hasPriceConfig(silent);
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlHelper#canSetSaleable(boolean, boolean)
	 */
	public boolean canSetSaleable(boolean silent, boolean saleable)
	{
		if (!saleable)
			return true;
		else {
			if (!hasPriceConfig(silent))
				return false;
			if (!hasLocalAccountantDelegate(silent))
				return false;
			if (!isLeaf(silent))
				return false;
			return true;
		}
	}

	public boolean canClose(boolean silent)
	{
		return true;
	}
}
