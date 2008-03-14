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
import java.util.Set;

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
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * A Table to view the Accounts a SummaryAccount summarys.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SummaryAccountSummedAccountsTable 
extends AbstractTableComposite<Account> 
{
	private static class ContentProvider extends TableContentProvider {
		private SummaryAccount summaryAccount;
		private Set<Account> summedAccounts;

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement != summaryAccount)
				summedAccounts = null;

			if (inputElement instanceof SummaryAccount) {
				summaryAccount = (SummaryAccount)inputElement;
				if (summedAccounts == null)
					summedAccounts = new HashSet<Account>(summaryAccount.getSummedAccounts());

				return getSummedAccounts().toArray();
			}
			return null;
		}
		
		public void addAccount(Account account) {
			summedAccounts.add(account);
		}
		
		public void removeAccount(Account account) {
			summedAccounts.remove(account);
		}
		
		public Collection<Account> getSummedAccounts() {
			return summedAccounts;
		}
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
				return ((Account)element).getName().getText(Locale.getDefault().getLanguage());
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
	
	public void setInput(SummaryAccount summaryAccount) {
		getTableViewer().setInput(summaryAccount);
	}
	
	public void addAccount(Account account) {
		((ContentProvider)getTableViewer().getContentProvider()).addAccount(account);
	}
	
	public void removeAccount(Account account) {
		((ContentProvider)getTableViewer().getContentProvider()).removeAccount(account);
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
	
	/**
	 * Returns a Collection of AnchorID that can be passed to
	 * {@link org.nightlabs.jfire.accounting.AccountingManager#setSummaryAccountSummedAccounts(org.nightlabs.jfire.transfer.id.AnchorID, java.util.Collection)}
	 * to set the list of summed Accounts for the SummaryAccount set here with {@link #setInput(SummaryAccount)}.
	 */
	public Collection<AnchorID> getSummedAccounts() {
		Collection<Account> summaryAccounts = ((ContentProvider)getTableViewer().getContentProvider()).getSummedAccounts();
		Collection<AnchorID> result = new HashSet<AnchorID>();
		for (Iterator<Account> iter = summaryAccounts.iterator(); iter.hasNext();) {
			Account account = iter.next();
			result.add((AnchorID)JDOHelper.getObjectId(account));
		}
		return result;
	}

}
