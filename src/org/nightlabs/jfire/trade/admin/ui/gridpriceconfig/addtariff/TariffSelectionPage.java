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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.dao.TariffDAO;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TariffSelectionPage extends DynamicPathWizardPage
{
	private Button createNewTariffRadio;
	private Button chooseExistingTariffRadio;
	private List<Tariff> tariffs = new ArrayList<Tariff>();
	private org.eclipse.swt.widgets.List tariffList;

	private Tariff selectedTariff = null;

	public TariffSelectionPage()
	{
		super(TariffSelectionPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff.TariffSelectionPage.title")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		createNewTariffRadio = new Button(page, SWT.RADIO);
		createNewTariffRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff.TariffSelectionPage.createNewTariffRadio.text")); //$NON-NLS-1$
		createNewTariffRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((AddTariffWizard)getWizard()).setCreateNewTariffEnabled(createNewTariffRadio.getSelection());
			}
		});

		chooseExistingTariffRadio = new Button(page, SWT.RADIO);
		chooseExistingTariffRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff.TariffSelectionPage.chooseExistingTariffRadio.text")); //$NON-NLS-1$
		chooseExistingTariffRadio.setSelection(true);

		tariffList = new org.eclipse.swt.widgets.List(page, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tariffList.setLayoutData(new GridData(GridData.FILL_BOTH));
		tariffList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				int selIdx = tariffList.getSelectionIndex();
				if (selIdx < 0)
					selectedTariff = null;
				else if (selIdx < tariffs.size())
					selectedTariff = tariffs.get(selIdx);

				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});

		tariffList.add(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff.TariffSelectionPage.pseudoEntry_loading")); //$NON-NLS-1$
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff.TariffSelectionPage.loadTariffsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				
				try {
					tariffs.clear();
					tariffs.addAll(TariffDAO.sharedInstance().getTariffs(Login.getLogin().getOrganisationID(), false, FETCH_GROUPS_TARIFF, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
				} catch (Exception e) {
					ExceptionHandlerRegistry.asyncHandleException(e);
					throw new RuntimeException(e);
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						tariffList.removeAll();
						for (Iterator<Tariff> it = tariffs.iterator(); it.hasNext(); ) {
							Tariff tariff = it.next();
							tariffList.add(tariff.getName().getText());
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
		return page;
	}

	public static final String[] FETCH_GROUPS_TARIFF = {
		FetchPlan.DEFAULT, Tariff.FETCH_GROUP_NAME
	};

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete()
	{
		return createNewTariffRadio.getSelection() || selectedTariff != null;
	}

	/**
	 * @return Returns the selectedTariff.
	 */
	public Tariff getSelectedTariff()
	{
		if (createNewTariffRadio.getSelection())
			return null;

		return selectedTariff;
	}
}
