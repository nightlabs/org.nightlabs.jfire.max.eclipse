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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class AddCurrencyWizard extends DynamicPathWizard
{
	private Dimension dimension;

	private CurrencySelectionPage currencySelectionPage;

	public AddCurrencyWizard(Dimension dimension)
	{
		this.dimension = dimension;
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	@Override
	public IDynamicPathWizardPage createWizardEntryPage()
	{
		currencySelectionPage = new CurrencySelectionPage();
		return currencySelectionPage;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		Currency currency = currencySelectionPage.getSelectedCurrency();
		if (currency == null)
			throw new IllegalStateException("currencySelectionPage.getSelectedCurrency() returned null!"); //$NON-NLS-1$

		dimension.guiFeedbackAddDimensionValue(
				new DimensionValue.CurrencyDimensionValue(dimension, currency));

		return true;
	}

}
