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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;
import org.nightlabs.jfire.trade.ui.customergroup.CustomerGroupDAO;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class AddCustomerGroupWizard extends DynamicPathWizard
{
	private Dimension dimension;
	
	private boolean createNewCustomerGroupEnabled = false;
	private CustomerGroupSelectionPage customerGroupSelectionPage;
	private CreateCustomerGroupPage createCustomerGroupPage;

	public AddCustomerGroupWizard(Dimension dimension)
	{
		this.dimension = dimension;
		setForcePreviousAndNextButtons(true);
	}

	private static class GroupHolder {
		public CustomerGroup customerGroup;
	}
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		try {
			final GroupHolder groupHolder = new GroupHolder();
			groupHolder.customerGroup = null;
			if (createNewCustomerGroupEnabled) {
				I18nTextBuffer customerGroupNameBuffer = createCustomerGroupPage.getCustomerGroupNameBuffer();

				groupHolder.customerGroup = new CustomerGroup(
						Login.getLogin().getOrganisationID(),
						ObjectIDUtil.makeValidIDString(
								customerGroupNameBuffer.getText(I18nText.DEFAULT_LANGUAGEID), true));

				customerGroupNameBuffer.copyTo(groupHolder.customerGroup.getName());
				
				getContainer().run(false, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						groupHolder.customerGroup = CustomerGroupDAO.sharedInstance().storeCustomerGroup(
								groupHolder.customerGroup, true, FETCH_GROUPS_CUSTOMER_GROUP,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
						);
					}
				});

//				AccountingManager am = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				customerGroup = am.storeCustomerGroup(customerGroup, true, new String[] {FetchPlan.ALL}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT); // TODO fetch-groups
			}
			else
				groupHolder.customerGroup = customerGroupSelectionPage.getSelectedCustomerGroup();

			if (groupHolder.customerGroup == null)
				throw new IllegalStateException("customerGroup was neither created nor selected!"); //$NON-NLS-1$

			dimension.guiFeedbackAddDimensionValue(
					new DimensionValue.CustomerGroupDimensionValue(dimension, groupHolder.customerGroup));

			return true;
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			throw new RuntimeException(x);
		}
	}

	public static final String[] FETCH_GROUPS_CUSTOMER_GROUP = {
		FetchPlan.DEFAULT, CustomerGroup.FETCH_GROUP_NAME
	};

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	@Override
	public IDynamicPathWizardPage createWizardEntryPage()
	{
		customerGroupSelectionPage = new CustomerGroupSelectionPage();
		createCustomerGroupPage = new CreateCustomerGroupPage();

		return customerGroupSelectionPage;
	}

	protected void setCreateNewCustomerGroupEnabled(boolean enabled) {
		removeAllDynamicWizardPages();
		if (enabled)
			addDynamicWizardPage(createCustomerGroupPage);

		createNewCustomerGroupEnabled = enabled;
		updateDialog();
	}

}
