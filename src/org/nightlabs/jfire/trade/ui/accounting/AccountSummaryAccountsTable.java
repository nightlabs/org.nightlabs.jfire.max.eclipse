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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.account.editor.AbstractAccountPageController;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.Util;

/**
 * A Table to view the SummaryAccounts of an Account.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AccountSummaryAccountsTable 
extends AbstractTableComposite<Account> 
{
	private class ContentProvider extends TableContentProvider {
		private Account account;
		
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Account) {
				account = (Account)inputElement;
				return account.getSummaryAccounts().toArray();
			}
			return null;
		}
		
		public void addSummaryAccount(SummaryAccount summaryAccount)
		{
			// take summaryAccount form DAO to fetch with the right fetchGroups
			// and make copy to avoid putting of wrong summaryAccount into cache
			summaryAccount = (SummaryAccount) AccountDAO.sharedInstance().getAccount(
					(AnchorID)JDOHelper.getObjectId(summaryAccount),
					AbstractAccountPageController.FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			summaryAccount = Util.cloneSerializable(summaryAccount);
			account.addSummaryAccount(summaryAccount);
		}
		
		public void removeSummaryAccount(SummaryAccount summaryAccount) {
			// take summaryAccount form DAO to fetch with the right fetchGroups
			// and make copy to avoid putting of wrong summaryAccount into cache
			summaryAccount = (SummaryAccount) AccountDAO.sharedInstance().getAccount(
					(AnchorID)JDOHelper.getObjectId(summaryAccount),
					AbstractAccountPageController.FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			summaryAccount = Util.cloneSerializable(summaryAccount);
			account.removeSummaryAccount(summaryAccount);
		}
		
		public Collection<SummaryAccount> getSummaryAccounts() {
			return account.getSummaryAccounts();
		}
	}
	
	private class LabelProvider extends TableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex != 0)
				return null;
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AccountSummaryAccountsTable.class, "summary"); //$NON-NLS-1$
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof SummaryAccount)
				return ((SummaryAccount)element).getName().getText(Locale.getDefault().getLanguage());
			return null;
		}
	}
	/**
	 * @param parent
	 * @param style
	 */
	public AccountSummaryAccountsTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public AccountSummaryAccountsTable(Composite parent, int style,
			boolean initTable) {
		super(parent, style, initTable);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.AccountSummaryAccountsTable.summaryAccountTableColumn.text")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}
	
	/**
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
	
	public void setInput(Account account) {
		getTableViewer().setInput(account);
	}
	
	public void addSummaryAccount(SummaryAccount summaryAccount) {
		((ContentProvider)getTableViewer().getContentProvider()).addSummaryAccount(summaryAccount);
		getTableViewer().refresh();
	}
	
	public void removeSummaryAccount(SummaryAccount summaryAccount) {
		((ContentProvider)getTableViewer().getContentProvider()).removeSummaryAccount(summaryAccount);
		getTableViewer().refresh();
	}

	/**
	 * @return The first selected SummaryAccount or null if none selected
	 */
	public Account getSelectedSummaryAccount() {
		IStructuredSelection selection = (IStructuredSelection)getTableViewer().getSelection();
		if (selection.size() > 0)
			return (Account)selection.getFirstElement();
		return null;
	}

	/**
	 * @return All selected SummaryAccount or null if none selected
	 */
	public Collection<Account> getSelectedSummaryAccounts() {
		IStructuredSelection selection = (IStructuredSelection)getTableViewer().getSelection();
		List<Account> result = new ArrayList<Account>();
		if (selection.size() > 0) {
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Account account = (Account) iter.next();
				result.add(account);
			}
		}
		return result;
	}
	
	/**
	 * Returns a Collection of AnchorID that can be passed to
	 * {@link org.nightlabs.jfire.accounting.AccountingManager#setAccountSummaryAccounts(org.nightlabs.jfire.transfer.id.AnchorID, java.util.Collection)}
	 * to set the list of SummaryAccounts for the account set here with {@link #setInput(Account)}.
	 */
	public Collection<AnchorID> getSummaryAccounts() {
		Collection<SummaryAccount> summaryAccounts = ((ContentProvider)getTableViewer().getContentProvider()).getSummaryAccounts();
		Collection<AnchorID> result = new HashSet<AnchorID>();
		for (Iterator<SummaryAccount> iter = summaryAccounts.iterator(); iter.hasNext();) {
			SummaryAccount summaryAccount = iter.next();
			result.add((AnchorID)JDOHelper.getObjectId(summaryAccount));
		}
		return result;
	}
}
