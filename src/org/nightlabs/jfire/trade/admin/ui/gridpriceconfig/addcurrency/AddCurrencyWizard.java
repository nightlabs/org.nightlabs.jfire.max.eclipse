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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.trade.admin.ui.editor.CurrencyCreateWizardPage;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue.CurrencyDimensionValue;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class AddCurrencyWizard extends DynamicPathWizard
{
	private Dimension<CurrencyDimensionValue> dimension;

	private boolean createNewCurrencyEnabled = false;
	private Currency newCurrency;
	
	private CurrencySelectionPage currencySelectionPage;
	private CurrencyCreateWizardPage createCurrencyPage;

	public AddCurrencyWizard(Dimension<CurrencyDimensionValue> dimension)
	{
		this.dimension = dimension;
		setForcePreviousAndNextButtons(true);
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	@Override
	public IDynamicPathWizardPage createWizardEntryPage()
	{
		currencySelectionPage = new CurrencySelectionPage();
		createCurrencyPage = new CurrencyCreateWizardPage();
		return currencySelectionPage;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		try {
			Currency currency = null;
			if (createNewCurrencyEnabled){
				getContainer().run(false, false, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						newCurrency = CurrencyDAO.sharedInstance().storeCurrency(
								createCurrencyPage.createCurrency(),
								true,
								null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new ProgressMonitorWrapper(monitor)
						);
					}
				});
				currency = newCurrency;
			}else{
				currency = currencySelectionPage.getSelectedCurrency();
				if (currency == null)
					throw new IllegalStateException("currencySelectionPage.getSelectedCurrency() returned null!"); //$NON-NLS-1$
			}
	
			dimension.guiFeedbackAddDimensionValue(
					new DimensionValue.CurrencyDimensionValue(dimension, currency));
			
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			return false;
		}

		return true;
	}

	protected void setCreateNewCurrencyEnabled(boolean enabled) {
		removeAllDynamicWizardPages();
		if (enabled)
			addDynamicWizardPage(createCurrencyPage);

		createNewCurrencyEnabled = enabled;
		updateDialog();
	}

}
