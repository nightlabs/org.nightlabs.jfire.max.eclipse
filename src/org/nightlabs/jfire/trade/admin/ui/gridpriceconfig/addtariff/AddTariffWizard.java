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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class AddTariffWizard extends DynamicPathWizard
{
	private Dimension dimension;
	private TariffSelectionPage tariffSelectionPage;
	private CreateTariffPage createTariffPage;
	private boolean createNewTariffEnabled = false;

	public AddTariffWizard(Dimension dimension)
	{
		this.dimension = dimension;
		setForcePreviousAndNextButtons(true);
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	public IDynamicPathWizardPage createWizardEntryPage()
	{
		tariffSelectionPage = new TariffSelectionPage();
		createTariffPage = new CreateTariffPage();
		return tariffSelectionPage;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try {
			Tariff tariff = null;
			if (createNewTariffEnabled) {
				I18nTextBuffer tariffNameBuffer = createTariffPage.getTariffNameBuffer();

				tariff = new Tariff(Login.getLogin().getOrganisationID(), Tariff.createTariffID());
//						BaseObjectID.makeValidIDString(
//								priceFragmentTypeNameBuffer.getText(I18nText.DEFAULT_LANGUAGEID), true));
				tariffNameBuffer.copyTo(tariff.getName());

				AccountingManager accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				tariff = accountingManager.storeTariff(tariff, true, new String[] {FetchPlan.ALL}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT); // TODO not ALL
			}
			else
				tariff = tariffSelectionPage.getSelectedTariff();

			if (tariff == null)
				throw new IllegalStateException("tariff was neither created nor selected!"); //$NON-NLS-1$

			dimension.guiFeedbackAddDimensionValue(
					new DimensionValue.TariffDimensionValue(dimension, tariff));

			return true;
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			throw new RuntimeException(x);
		}
	}

	protected void setCreateNewTariffEnabled(boolean enabled)
	{
		removeAllDynamicWizardPages();
		if (enabled)
			addDynamicWizardPage(createTariffPage);

		createNewTariffEnabled = enabled;
		updateDialog();
	}
}
