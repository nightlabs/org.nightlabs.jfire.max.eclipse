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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.edit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountSearchFilter;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.accounting.CurrencyCombo;
import org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SelectCurrencyAndAccountPage 
extends DynamicPathWizardPage 
{
	public static final String PROPERTY_CURRENCY = "currency"; //$NON-NLS-1$
	public static final String PROPERTY_ACCOUNT = "account"; //$NON-NLS-1$

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private Label currencyLabel; 
	private CurrencyCombo currencyCombo;
//	private AccountTable accountTable;
	private AccountListComposite accountTable;
	private boolean allowCurrencySelection;
	private boolean optionalAccount;
	private Button doNotSpecifyThisAccountCheckBox;

	public SelectCurrencyAndAccountPage(String pageName, String title, String description, boolean allowCurrencySelection, boolean optionalAccount) {
		super(pageName, title);
		setDescription(description);
		this.allowCurrencySelection = allowCurrencySelection;
		this.optionalAccount = optionalAccount;
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		
		SelectionListener selectionListener = new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				propertyChangeSupport.firePropertyChange(PROPERTY_ACCOUNT, null, getAccount());
				getContainer().updateButtons();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};

		if (optionalAccount) {
			doNotSpecifyThisAccountCheckBox = new Button(wrapper, SWT.CHECK);
			doNotSpecifyThisAccountCheckBox.setText("I do not want to specify this account.");
			doNotSpecifyThisAccountCheckBox.setSelection(true);
			doNotSpecifyThisAccountCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			doNotSpecifyThisAccountCheckBox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					propertyChangeSupport.firePropertyChange(PROPERTY_ACCOUNT, null, getAccount());
					getContainer().updateButtons();
				}
			});
		}

		currencyLabel = new Label(wrapper, SWT.WRAP);
		currencyLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		currencyLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectCurrencyAndAccountPage.currencyLabel.text")); //$NON-NLS-1$

		currencyCombo = new CurrencyCombo(wrapper, SWT.NONE);
		currencyCombo.setEnabled(allowCurrencySelection);
		currencyCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				currency = currencyCombo.getSelectedCurrency();
				if (currency != null) {
					// TODO: Implement this to AccountListComposite
//					accountTable.getContentProvider().setFilterCurrency(currencyCombo.getSelectedCurrency().getCurrencyID());
					loadAccounts();
				}
				propertyChangeSupport.firePropertyChange(PROPERTY_CURRENCY, null, getCurrency());
				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});
//		currencyCombo.getGridData().grabExcessHorizontalSpace = false;

//		accountTable = new AccountTable(wrapper, SWT.NONE, Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_IN);
		accountTable = new AccountListComposite(wrapper, SWT.NONE);
		accountTable.getTable().addSelectionListener(selectionListener);

		return wrapper;
	}

	private void loadAccounts()
	{
		Job loadAccountsJob = new Job("Loading Accounts") {
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						accountTable.setInput("Loading accounts...");
					}
				});

				AccountSearchFilter accountSearchFilter = null;
				if (getCurrency() != null) {
					accountSearchFilter = new AccountSearchFilter();
					accountSearchFilter.setCurrencyID(getCurrency().getCurrencyID());
				}

				final List<Account> accounts = accountSearchFilter == null ? new ArrayList<Account>(0) : AccountDAO.sharedInstance().getAccounts(
						accountSearchFilter,
						new String[] { FetchPlan.DEFAULT, Account.FETCH_GROUP_NAME, Account.FETCH_GROUP_CURRENCY, Account.FETCH_GROUP_OWNER, LegalEntity.FETCH_GROUP_PERSON },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						accountTable.setInput(accounts);
//						accountTable.refresh();
					}
				});

				return Status.OK_STATUS;
			}
		};
		loadAccountsJob.schedule();
	}

	@Override
	public boolean isPageComplete() {
		if (currencyCombo == null)
			return false;

		if (optionalAccount) {
			return
					doNotSpecifyThisAccountCheckBox.getSelection() ||
					(
							currency != null && 
							getAccount() != null
					);
		}

		return
				currency != null && 
				getAccount() != null;
	}

	public Currency getCurrency() {
		return currency;
	}
	private Currency currency;

	public void setCurrency(Currency currency) {
		this.currency = currency;
		currencyCombo.setSelectedCurrency(currency);
		loadAccounts();
	}

	public Account getAccount() {
		if (optionalAccount && doNotSpecifyThisAccountCheckBox.getSelection())
			return null;

		if (accountTable.getSelectedElements().isEmpty())
			return null;

		return accountTable.getSelectedElements().iterator().next();
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

//	protected String getDefaultPageMessage() {
//		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectCurrencyAndAccountPage.description"); //$NON-NLS-1$
//	}
	
}
