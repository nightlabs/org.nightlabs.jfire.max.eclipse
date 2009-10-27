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

package org.nightlabs.jfire.trade.ui.accounting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.trade.ui.overview.account.AccountEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.account.AccountEntryViewer;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class AccountSearchDialog
extends ResizableTrayDialog
{
	private AccountTypeID accountTypeID;

	/**
	 * @param anchorTypeID
	 */
	public AccountSearchDialog(AccountTypeID accountTypeID) {
		super(RCPUtil.getActiveShell(), Messages.RESOURCE_BUNDLE);
		this.accountTypeID = accountTypeID;

		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public AccountSearchDialog() {
		super(RCPUtil.getActiveShell(), Messages.RESOURCE_BUNDLE);

		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private AccountQuery query = null;
	protected void applyAccountTypeID()
	{
		if (query == null)
			query = new AccountQuery();

		//Prepare the query for the accountType
		query.setAllFieldsDisabled();
		query.setFieldEnabled(AccountQuery.FieldName.accountTypeID, Boolean.TRUE);
		query.setAccountTypeID(accountTypeID);

		final QueryCollection<AccountQuery> queries =	new QueryCollection<AccountQuery>(Account.class);
		queries.add(query);

		//Update the filter UI
		accountEntryViewer.getQueryProvider().loadQueries(queries);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.AccountSearchDialog.loadingAccountsJob.name")){ //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final Collection<Account> accounts = AccountDAO.sharedInstance().getAccountsForQueries(
						queries,
						AccountEntryViewer.FETCH_GROUPS_ACCOUNTS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor);
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						accountEntryViewer.getListComposite().setInput(accounts);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private AccountEntryViewer accountEntryViewer;
	@Override
	protected Control createDialogArea(Composite parent)
	{
		accountEntryViewer = new AccountEntryViewer(
				new AccountEntryFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(
					AbstractTableComposite<Account> tableComposite) {
				tableComposite.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						okPressed();
					}
				});

				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						selectedAccounts = accountEntryViewer.getListComposite().getSelectedElements();
						getButton(IDialogConstants.OK_ID).setEnabled(selectedAccounts != null);
					}
				});
			}
		};

		Composite comp = accountEntryViewer.createComposite(parent);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gridData);

		if (accountTypeID != null)
			applyAccountTypeID();

		return comp;
	}

	public static final int SEARCH_ID = IDialogConstants.CLIENT_ID + 1;
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		Button searchButton = createButton(parent, SEARCH_ID, "Search", true); //$NON-NLS-1$
		searchButton.addSelectionListener(searchButtonListener);

		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	private SelectionListener searchButtonListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			accountEntryViewer.search();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	private Collection<Account> selectedAccounts = null;
	public Collection<Account> getSelectedAccounts() {
		return selectedAccounts;
	}

	/**
	 * Opens a new AccountSearchDialog and returns the selected Account.
	 * @return One selected Account.
	 */
	public static Account searchAccount(AccountTypeID accountTypeID) {
		Collection<Account> accountSet = searchAccounts(accountTypeID);
		if (!accountSet.isEmpty())
			return accountSet.iterator().next();

		return null;
	}

	/**
	 * Opens a new AccountSearchDialog and returns all selected Account.
	 * @return All selected Account.
	 */
	public static Collection<Account> searchAccounts(AccountTypeID accountTypeID) {
		final Set<Account> accountSet = new HashSet<Account>();
		AccountSearchDialog dialog = new AccountSearchDialog(accountTypeID);
		int returnCode = dialog.open();
		if (returnCode == Window.OK) {
			return dialog.getSelectedAccounts();
		}
		return accountSet;
	}

}
