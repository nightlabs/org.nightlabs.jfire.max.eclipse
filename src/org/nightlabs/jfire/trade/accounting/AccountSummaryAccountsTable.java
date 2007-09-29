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

package org.nightlabs.jfire.trade.accounting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
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
import org.nightlabs.jfire.trade.TradePlugin;
import org.nightlabs.jfire.trade.account.editor.AbstractAccountPageController;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.Util;

/**
 * A Table to view the SummaryAccounts of an Account.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AccountSummaryAccountsTable extends AbstractTableComposite {

	private class ContentProvider extends TableContentProvider {
		private Account account;
		
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

//	/**
//	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(TableViewer, org.eclipse.swt.widgets.Table)
//	 */
//	protected void createTableColumns(TableViewer tableViewer, Table table) {
//		TableColumn tc = new TableColumn(table, SWT.LEFT);
//		tc.setText(Messages.getString("org.nightlabs.jfire.trade.accounting.AccountSummaryAccountsTable.summaryAccountTableColumn.text")); //$NON-NLS-1$
////		table.setLayout(new WeightedTableLayout(new int[] {1}));
//		TableLayout tl = new TableLayout();
//		tl.addColumnData(new ColumnWeightData(1));
//		table.setLayout(tl);
//	}

	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.accounting.AccountSummaryAccountsTable.summaryAccountTableColumn.text")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}
	
	/**
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
	
	public void setInput(Account account) {
		tableViewer.setInput(account);
	}
	
	public void addSummaryAccount(SummaryAccount summaryAccount) {
		((ContentProvider)tableViewer.getContentProvider()).addSummaryAccount(summaryAccount);
		tableViewer.refresh();
	}
	
	public void removeSummaryAccount(SummaryAccount summaryAccount) {
		((ContentProvider)tableViewer.getContentProvider()).removeSummaryAccount(summaryAccount);
		tableViewer.refresh();
	}

	/**
	 * @return The first selected SummaryAccount or null if none selected
	 */
	public Account getSelectedSummaryAccount() {
		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		if (selection.size() > 0)
			return (Account)selection.getFirstElement();
		return null;
	}

	/**
	 * @return All selected SummaryAccount or null if none selected
	 */
	public Collection getSelectedSummaryAccounts() {
		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		List result = new ArrayList();
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
	public Collection getSummaryAccounts() {
		Collection summaryAccounts = ((ContentProvider)tableViewer.getContentProvider()).getSummaryAccounts();
		Collection result = new HashSet();
		for (Iterator iter = summaryAccounts.iterator(); iter.hasNext();) {
			SummaryAccount summaryAccount = (SummaryAccount) iter.next();
			result.add(JDOHelper.getObjectId(summaryAccount));
		}
		return result;	
	}
}
