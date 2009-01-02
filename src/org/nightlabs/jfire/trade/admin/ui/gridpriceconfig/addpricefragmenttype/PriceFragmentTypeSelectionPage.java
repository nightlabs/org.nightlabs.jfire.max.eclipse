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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype;

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
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PriceFragmentTypeSelectionPage extends DynamicPathWizardPage
{
	private Button createNewPriceFragmentTypeRadio;
	private Button chooseExistingPriceFragmentTypeRadio;
	private PriceFragmentType selectedPriceFragmentType = null;
	private List priceFragmentTypes = new ArrayList();
	private org.eclipse.swt.widgets.List priceFragmentTypeList;

	public PriceFragmentTypeSelectionPage()
	{
		super(PriceFragmentTypeSelectionPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.PriceFragmentTypeSelectionPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.PriceFragmentTypeSelectionPage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		createNewPriceFragmentTypeRadio = new Button(page, SWT.RADIO);
		createNewPriceFragmentTypeRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.PriceFragmentTypeSelectionPage.createNewPriceFragmentTypeRadio.text")); //$NON-NLS-1$
		createNewPriceFragmentTypeRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				((AddPriceFragmentTypeWizard)getWizard()).setCreateNewPriceFragmentTypeEnabled(
						createNewPriceFragmentTypeRadio.getSelection());
			}
		});

		chooseExistingPriceFragmentTypeRadio = new Button(page, SWT.RADIO);
		chooseExistingPriceFragmentTypeRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.PriceFragmentTypeSelectionPage.chooseExistingPriceFragmentTypeRadio.text")); //$NON-NLS-1$
		chooseExistingPriceFragmentTypeRadio.setSelection(true);

		priceFragmentTypeList = new org.eclipse.swt.widgets.List(page, SWT.BORDER);
		priceFragmentTypeList.setLayoutData(new GridData(GridData.FILL_BOTH));
		priceFragmentTypeList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				int selIdx = priceFragmentTypeList.getSelectionIndex();
				if (selIdx < 0)
					selectedPriceFragmentType = null;
				else if (selIdx < priceFragmentTypes.size())
					selectedPriceFragmentType = (PriceFragmentType) priceFragmentTypes.get(selIdx);

				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});

		priceFragmentTypeList.add(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.PriceFragmentTypeSelectionPage.pseudoEntry_loading")); //$NON-NLS-1$
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.PriceFragmentTypeSelectionPage.loadPriceFragmentTypesJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					AccountingManager accountingManager = JFireEjbUtil.getBean(AccountingManager.class, Login.getLogin().getInitialContextProperties());
					priceFragmentTypes.clear();
					// TODO not ALL!
					// TODO use DAO
					priceFragmentTypes.addAll(accountingManager.getPriceFragmentTypes(null, new String[] {FetchPlan.ALL}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
				} catch (Exception e) {
					ExceptionHandlerRegistry.asyncHandleException(e);
					throw new RuntimeException(e);
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						priceFragmentTypeList.removeAll();
						for (Iterator it = priceFragmentTypes.iterator(); it.hasNext(); ) {
							PriceFragmentType priceFragmentType = (PriceFragmentType) it.next();
							priceFragmentTypeList.add(priceFragmentType.getName().getText());
						}
					}
				});
				return Status.OK_STATUS;
			}
			
		};
		loadJob.schedule();
		return page;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete()
	{
		return createNewPriceFragmentTypeRadio.getSelection() || selectedPriceFragmentType != null;
	}

	/**
	 * @return Returns the selectedPriceFragmentType.
	 */
	public PriceFragmentType getSelectedPriceFragmentType()
	{
		if (createNewPriceFragmentTypeRadio.getSelection())
			return null;

		return selectedPriceFragmentType;
	}
}
