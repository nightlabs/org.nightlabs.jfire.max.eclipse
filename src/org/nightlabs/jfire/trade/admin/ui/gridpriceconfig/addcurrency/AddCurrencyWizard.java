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
	private Currency currency;

	private CurrencySelectionPage currencySelectionPage;
	private CurrencyCreateWizardPage createCurrencyPage;

	public AddCurrencyWizard() { }

	public AddCurrencyWizard(final Dimension<CurrencyDimensionValue> dimension)
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
			if (createNewCurrencyEnabled){
				System.out.println("creating new currency...");
				getContainer().run(false, false, new IRunnableWithProgress() {
					@Override
					public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						currency = CurrencyDAO.sharedInstance().storeCurrency(
								createCurrencyPage.createCurrency(),
								true,
								null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new ProgressMonitorWrapper(monitor)
						);
					}
				});
			} else{
				currency = currencySelectionPage.getSelectedCurrency();
				if (currency == null) {
					throw new IllegalStateException("currencySelectionPage.getSelectedCurrency() returned null!"); //$NON-NLS-1$
				}
			}

			// TODO set dimension in AddCurrencyWizard() constructor implicitly called by subclasses if necessary
			if (dimension != null) {
				dimension.guiFeedbackAddDimensionValue(
					new DimensionValue.CurrencyDimensionValue(dimension, currency));
			}

		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			return false;
		}

		return true;
	}

	protected void setCreateNewCurrencyEnabled(final boolean enabled) {
		removeAllDynamicWizardPages();
		if (enabled) {
			createCurrencyPage = new CurrencyCreateWizardPage();
			addDynamicWizardPage(createCurrencyPage);
		}
		createNewCurrencyEnabled = enabled;
		updateDialog();
	}

	public CurrencySelectionPage getCurrencySelectionPage() {
		return currencySelectionPage;
	}

	public void setCurrencySelectionPage(final CurrencySelectionPage currencySelectionPage) {
		this.currencySelectionPage = currencySelectionPage;
	}

	public Currency getCurrency() {
		return currency;
	}
}
