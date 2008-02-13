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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.customergroup.CustomerGroupDAO;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CustomerGroupSelectionPage extends DynamicPathWizardPage
{
	private List<CustomerGroup> customerGroups = new ArrayList<CustomerGroup>();
	private org.eclipse.swt.widgets.List customerGroupList;

	private Button createNewCustomerGroupRadio;
	private Button chooseExistingCustomerGroupRadio;

	private CustomerGroup selectedCustomerGroup = null;

	public CustomerGroupSelectionPage()
	{
		super(CustomerGroupSelectionPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.CustomerGroupSelectionPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.CustomerGroupSelectionPage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		createNewCustomerGroupRadio = new Button(page, SWT.RADIO);
		createNewCustomerGroupRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.CustomerGroupSelectionPage.createNewCustomerGroupRadio.text")); //$NON-NLS-1$
		createNewCustomerGroupRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((AddCustomerGroupWizard)getWizard()).setCreateNewCustomerGroupEnabled(createNewCustomerGroupRadio.getSelection());
			}
		});
		chooseExistingCustomerGroupRadio = new Button(page, SWT.RADIO);
		chooseExistingCustomerGroupRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.CustomerGroupSelectionPage.chooseExistingCustomerGroupRadio.text")); //$NON-NLS-1$
		chooseExistingCustomerGroupRadio.setSelection(true);

		customerGroupList = new org.eclipse.swt.widgets.List(page, SWT.BORDER);
		customerGroupList.setLayoutData(new GridData(GridData.FILL_BOTH));
		customerGroupList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				int selIdx = customerGroupList.getSelectionIndex();
				if (selIdx < 0)
					selectedCustomerGroup = null;
				else if (selIdx < customerGroups.size())
					selectedCustomerGroup = customerGroups.get(selIdx);

				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});

		customerGroupList.add(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.CustomerGroupSelectionPage.pseudoEntry_loading")); //$NON-NLS-1$
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.CustomerGroupSelectionPage.loadCustomerGroupsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					customerGroups.clear();
					customerGroups.addAll(CustomerGroupDAO.sharedInstance().getCustomerGroups(Login.getLogin().getOrganisationID(), false, FETCH_GROUPS_CUSTOMER_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
				} catch (Exception e) {
					ExceptionHandlerRegistry.asyncHandleException(e);
					throw new RuntimeException(e);
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						customerGroupList.removeAll();
						for (Iterator it = customerGroups.iterator(); it.hasNext(); ) {
							CustomerGroup customerGroup = (CustomerGroup) it.next();
							customerGroupList.add(customerGroup.getName().getText());
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
		return page;
	}

	public static final String[] FETCH_GROUPS_CUSTOMER_GROUP = {
		FetchPlan.DEFAULT, CustomerGroup.FETCH_GROUP_NAME
	};

	/**
	 * @return Returns the selectedCustomerGroup.
	 */
	public CustomerGroup getSelectedCustomerGroup()
	{
		if (createNewCustomerGroupRadio.getSelection())
			return null;
		return selectedCustomerGroup;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete()
	{
		if (createNewCustomerGroupRadio == null)
			return false;
		return createNewCustomerGroupRadio.getSelection() || selectedCustomerGroup != null;
	}
}
