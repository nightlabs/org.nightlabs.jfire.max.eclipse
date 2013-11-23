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
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * Composite to view and manage an Accounts SummaryAccounts.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class AccountSummaryAccountsEditComposite
extends XComposite
//implements IAccountContainer
{
	private XComposite tableWrapper;
	private AccountSummaryAccountsTable summaryAccountsTable;
	private XComposite buttonWrapper;
	private Button addButton;
	private Button removeButton;
	private Account currAccount;
	private IDirtyStateManager dirtyStateManager;
	private boolean showButtons = true;
	
	public Account getAccount() {
		return currAccount;
	}
	
	public void addAccount() {
		if (currAccount == null)
			return;

		Collection<Account> accounts = AccountSearchDialog.searchAccounts(getShell(), AccountType.ACCOUNT_TYPE_ID_SUMMARY);
		for (Iterator<Account> iter = accounts.iterator(); iter.hasNext();) {
			Account account = iter.next();
			if (account instanceof SummaryAccount) {
				summaryAccountsTable.addSummaryAccount((SummaryAccount)account);
				if (dirtyStateManager != null)
					dirtyStateManager.markDirty();
			}
		}
		summaryAccountsTable.refresh();
	}
	
	private SelectionListener addButtonListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			addAccount();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	public void removeAccount()
	{
		if (!summaryAccountsTable.getSelectedSummaryAccounts().isEmpty()) {
			for (Iterator<Account> iter = summaryAccountsTable.getSelectedSummaryAccounts().iterator(); iter.hasNext();) {
				SummaryAccount summaryAccount = (SummaryAccount) iter.next();
				summaryAccountsTable.removeSummaryAccount(summaryAccount);
			}
			summaryAccountsTable.refresh();
			if (dirtyStateManager != null)
				dirtyStateManager.markDirty();
		}
	}
	
	private SelectionListener removeButtonListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			removeAccount();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	/**
	 * @param parent
	 * @param style
	 */
	public AccountSummaryAccountsEditComposite(Composite parent, int style) {
		this(parent, style, null, true);
	}

	/**
	 * @param parent
	 * @param style
	 * @param doSetLayoutData
	 */
	public AccountSummaryAccountsEditComposite(Composite parent, int style,
			IDirtyStateManager dirtyStateManager, boolean showButtons)
	{
//		super(parent, style, true);
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		this.showButtons = showButtons;
		Label label = new Label(this, SWT.WRAP);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.AccountSummaryAccountsEditComposite.descriptionLabel.text")); //$NON-NLS-1$
		
		tableWrapper = new XComposite(this, SWT.NONE);
		tableWrapper.getGridLayout().numColumns = 2;
		summaryAccountsTable = new AccountSummaryAccountsTable(tableWrapper, SWT.NONE);
		
		buttonWrapper = new XComposite(tableWrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonWrapper.getGridData().grabExcessHorizontalSpace = false;
		
		if (showButtons) {
			addButton = new Button(buttonWrapper, SWT.PUSH);
			addButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.AccountSummaryAccountsEditComposite.addButton.text")); //$NON-NLS-1$
			addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addButton.addSelectionListener(addButtonListener);
			
			removeButton = new Button(buttonWrapper, SWT.PUSH);
			removeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.AccountSummaryAccountsEditComposite.removeButton.text")); //$NON-NLS-1$
			removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeButton.addSelectionListener(removeButtonListener);
		}
	}

	public void setAccount(Account account) {
		currAccount = account;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!summaryAccountsTable.isDisposed())
					summaryAccountsTable.setInput(currAccount);
			}
		});
	}
	
//	/**
//	 * @deprecated use {@link #setAccount(Account)} instead
//	 * @see org.nightlabs.base.ui.entitylist.EntityManager#setEntity(java.lang.Object)
//	 */
//	public void setEntity(Object entity) {
//		if (!(entity instanceof AnchorID))
//			throw new IllegalArgumentException(this.getClass().getName()+" can only manage entities of Type AnchorID. Entity passed was "+entity.getClass().getName()); //$NON-NLS-1$
//		AnchorID accountID = (AnchorID)entity;
//		Account account = null;
//		try {
//			account = AccountingUtil.getAccountingManager().getAccount(accountID, new String[] {
//					FetchPlan.DEFAULT,
//					Account.FETCH_GROUP_SUMMARY_ACCOUNTS,
//					Account.FETCH_GROUP_NAME
//			}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		setAccount(account);
//	}
//
//	/**
//	 * @deprecated
//	 * @see org.nightlabs.base.ui.entitylist.EntityManager#save()
//	 */
//	public void save() throws ModuleException, RemoteException {
//		if (currAccount == null)
//			return;
//
//		AccountingUtil.getAccountingManager().setAccountSummaryAccounts((AnchorID)JDOHelper.getObjectId(currAccount), summaryAccountsTable.getSummaryAccounts());
//	}

}
