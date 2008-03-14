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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
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
extends CenteredDialog
{
	private AccountTypeID accountTypeID;

	/**
	 * @param anchorTypeID
	 */
	public AccountSearchDialog(AccountTypeID accountTypeID) {
		super(RCPUtil.getActiveShell());
		this.accountTypeID = accountTypeID;
	}

	public AccountSearchDialog() {
		super(RCPUtil.getActiveShell());
	}
	
	protected void applyAccountTypeID()
	{
		AccountQuery query = new AccountQuery();
		query.setAccountTypeID(accountTypeID);
		final QueryCollection<Account, AccountQuery> queries = new QueryCollection<Account, AccountQuery>();
		queries.add(query);

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
				new AccountEntryFactory().createEntry());
		Composite comp = accountEntryViewer.createComposite(parent);
		if (accountTypeID != null)
			applyAccountTypeID();
		return comp;
	}
		
	@Override
	protected void okPressed()
	{
		selectedAccounts = accountEntryViewer.getListComposite().getSelectedElements();
		super.okPressed();
	}
	
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
			return (Account)accountSet.iterator().next();
		else
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
