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
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * Composite to view and manage a SummaryAccounts summed Accounts.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class SummaryAccountSummedAccountsEditComposite 
extends XComposite
//implements IAccountContainer
{
	private XComposite tableWrapper;
	private SummaryAccountSummedAccountsTable summedAccountsTable;
	private XComposite buttonWrapper;
	private Button addButton;
	private Button removeButton;
	private SummaryAccount currSummaryAccount;
	private IDirtyStateManager dirtyStateManager = null;
	private boolean showButtons = true;
	
	public void addAccount() 
	{
		if (currSummaryAccount == null)
			return;

		Collection accounts = AccountSearchDialog.searchAccounts(null); // Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_IN); // TODO Account: Type?
		if (accounts != null && !accounts.isEmpty()) {
			for (Iterator iter = accounts.iterator(); iter.hasNext();) {
				Account account = (Account) iter.next();
				summedAccountsTable.addAccount(account);
			}
			summedAccountsTable.refresh();			
			if (dirtyStateManager != null)
				dirtyStateManager.markDirty();
		}
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
		if (!summedAccountsTable.getSelectedSummedAccounts().isEmpty()) {
			for (Iterator iter = summedAccountsTable.getSelectedSummedAccounts().iterator(); iter.hasNext();) {
				Account account = (Account) iter.next();
				summedAccountsTable.removeAccount(account);
			}
			summedAccountsTable.refresh();
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
		
	public SummaryAccountSummedAccountsEditComposite(Composite parent, int style) {
		this(parent, style, null, true);
	}
	
	/**
	 * @param parent
	 * @param style
	 * @param doSetLayoutData
	 */
	public SummaryAccountSummedAccountsEditComposite(Composite parent, int style, 
			IDirtyStateManager dirtyStateManager, boolean showButtons) 
	{
		super(parent, style);		
		this.dirtyStateManager = dirtyStateManager;
		this.showButtons = showButtons;
		Label label = new Label(this, SWT.WRAP);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.SummaryAccountSummedAccountsEditComposite.descriptionLabel.text")); //$NON-NLS-1$

		tableWrapper = new XComposite(this, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		tableWrapper.getGridLayout().numColumns = 2;
		summedAccountsTable = new SummaryAccountSummedAccountsTable(tableWrapper, SWT.NONE, true);
		
		buttonWrapper = new XComposite(tableWrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonWrapper.getGridData().grabExcessHorizontalSpace = false;
		
		if (showButtons) {
			addButton = new Button(buttonWrapper, SWT.PUSH);
			addButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.SummaryAccountSummedAccountsEditComposite.addButton.text")); //$NON-NLS-1$
			addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addButton.addSelectionListener(addButtonListener);

			removeButton = new Button(buttonWrapper, SWT.PUSH);
			removeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.SummaryAccountSummedAccountsEditComposite.removeButton.text")); //$NON-NLS-1$
			removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeButton.addSelectionListener(removeButtonListener);			
		}
	}

	public void setSummaryAccount(SummaryAccount summaryAccount) {
		this.currSummaryAccount = summaryAccount;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				summedAccountsTable.setInput(currSummaryAccount);
			}
		});
	}	
	
//	/**
//	 * @deprecated use {@link #setSummaryAccount(SummaryAccount)} instead
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
//					Account.FETCH_GROUP_NAME,
//					SummaryAccount.FETCH_GROUP_SUMMED_ACCOUNTS
//			}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		if (!(account instanceof SummaryAccount))
//			throw new IllegalStateException("Passed anchorID in setEntity() was not the ID of a SummaryAccount, server returned "+account.getClass().getName()+" instead."); //$NON-NLS-1$ //$NON-NLS-2$
//		setSummaryAccount((SummaryAccount)account);
//	}
//	
//	/**
//	 * @deprecated
//	 * @see org.nightlabs.base.ui.entitylist.EntityManager#save()
//	 */
//	public void save() throws ModuleException, RemoteException {
//		if (currSummaryAccount == null)
//			return;
//
//		AccountingUtil.getAccountingManager().setSummaryAccountSummedAccounts((AnchorID)JDOHelper.getObjectId(currSummaryAccount), summedAccountsTable.getSummedAccounts());
//	}
}
