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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * A Table to view the Accounts a SummaryAccount summarys.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 *
 */
public class SummaryAccountSummedAccountsTable 
extends AbstractTableComposite<Account> 
{	
	private static class ContentProvider extends ArrayContentProvider {
	}	
	
	private static class LabelProvider extends TableLabelProvider {
		
		public Image getSummaryAccountImg() {
			return SharedImages.getSharedImage(TradePlugin.getDefault(), SummaryAccountSummedAccountsTable.class, "normal"); //$NON-NLS-1$
		}
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex != 0)
				return null;
			return getSummaryAccountImg();
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Account)
				return ((Account)element).getName().getText(NLLocale.getDefault().getLanguage());
			return null;
		}
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public SummaryAccountSummedAccountsTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public SummaryAccountSummedAccountsTable(Composite parent, int style,
			boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.SummaryAccountSummedAccountsTable.summedAccountTableColumn.text")); //$NON-NLS-1$
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
	
	private SummaryAccount summaryAccount;
	
	public void setInput(SummaryAccount summaryAccount) 
	{
		this.summaryAccount = summaryAccount;
		getTableViewer().setInput(summaryAccount.getSummedAccounts());
	}

	public void addAccounts(final Collection<Account> accounts) 
	{
		Job job = new Job("Load Accounts") 
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception 
			{
				Collection<AnchorID> accountIDs = NLJDOHelper.getObjectIDList(accounts);
				Collection<Account> accounts = AccountDAO.sharedInstance().getAccounts(accountIDs, 
						new String[] {FetchPlan.DEFAULT, Account.FETCH_GROUP_SUMMARY_ACCOUNTS, Account.FETCH_GROUP_NAME}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor());
				for (Account account : accounts) {
					summaryAccount.addSummedAccount(account);	
				}	
				
				getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();		
	}
	
	public void addAccount(final Account account) 
	{
		addAccounts(Collections.singleton(account));
	}
	
	public void removeAccounts(final Collection<Account> accounts) 
	{
		Job job = new Job("Load Accounts") 
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception 
			{
				Collection<AnchorID> accountIDs = NLJDOHelper.getObjectIDList(accounts);
				Collection<Account> accounts = AccountDAO.sharedInstance().getAccounts(accountIDs, 
						new String[] {FetchPlan.DEFAULT, Account.FETCH_GROUP_SUMMARY_ACCOUNTS, Account.FETCH_GROUP_NAME}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor());
				for (Account account : accounts) {
					summaryAccount.removeSummedAccount(account);	
				}	
				
				getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();		
	}
	
	public void removeAccount(final Account account) 
	{
		removeAccounts(Collections.singleton(account));
	}
	
	/**
	 * @return The first selected summed Account or null if none selected
	 */
	public Account getSelectedSummedAccount() {
		IStructuredSelection selection = (IStructuredSelection)getTableViewer().getSelection();
		if (selection.size() > 0)
			return (Account)selection.getFirstElement();
		return null;
	}

	/**
	 * @return All selected summed Accounts or null if none selected
	 */
	public Collection<Account> getSelectedSummedAccounts() {
		IStructuredSelection selection = (IStructuredSelection)getTableViewer().getSelection();
		List<Account> result = new ArrayList<Account>();
		if (selection.size() > 0) {
			for (Iterator<Account> iter = selection.iterator(); iter.hasNext();) {
				Account account = iter.next();
				result.add(account);
			}
			return result;
		}
		return null;
	}
	
//	/**
//	 * Returns a Collection of AnchorID that can be passed to
//	 * {@link org.nightlabs.jfire.accounting.AccountingManager#setSummaryAccountSummedAccounts(org.nightlabs.jfire.transfer.id.AnchorID, java.util.Collection)}
//	 * to set the list of summed Accounts for the SummaryAccount set here with {@link #setInput(SummaryAccount)}.
//	 */
//	public Collection<AnchorID> getSummedAccounts() {
//		Collection<Account> summaryAccounts = summedAccounts;
//		Collection<AnchorID> result = new HashSet<AnchorID>();
//		for (Iterator<Account> iter = summaryAccounts.iterator(); iter.hasNext();) {
//			Account account = iter.next();
//			result.add((AnchorID)JDOHelper.getObjectId(account));
//		}
//		return result;
//	}

}
