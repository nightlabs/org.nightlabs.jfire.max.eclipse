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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de> 
 */
public class AccountConfigurationComposite 
extends XComposite
{	
	private StackLayout stackLayout;
	private XComposite normalAccountWrapper;
	private AccountSummaryAccountsEditComposite normalSummaryAccountsComp;
	private XComposite summaryAccountWrapper;
	private SummaryAccountSummedAccountsEditComposite summarySummedAccountsComp;
	private AccountSummaryAccountsEditComposite summarySummaryAccountsComp;
	private boolean editingSummaryAccount = false;
	private IDirtyStateManager dirtyStateManager;
	private boolean showButtons = true;
	
	/**
	 * @param parent
	 * @param style
	 */
	public AccountConfigurationComposite(Composite parent, int style) {
		this(parent, style, null, true);
	}

	/**
	 * @param parent
	 * @param style
	 * @param doSetLayoutData
	 */
	public AccountConfigurationComposite(Composite parent, int style,
			IDirtyStateManager dirtyStateManager, boolean showButtons) 
	{
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		this.showButtons = showButtons;
		stackLayout = new StackLayout();
		this.setLayout(stackLayout);
		
		normalAccountWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		normalSummaryAccountsComp = new AccountSummaryAccountsEditComposite(
				normalAccountWrapper, SWT.NONE, dirtyStateManager, showButtons);
		
		summaryAccountWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		summaryAccountWrapper.getGridLayout().numColumns = 2;
		summaryAccountWrapper.getGridLayout().makeColumnsEqualWidth = true;
		
		summarySummedAccountsComp = new SummaryAccountSummedAccountsEditComposite(
				summaryAccountWrapper, SWT.NONE, dirtyStateManager, true);
		summarySummaryAccountsComp = new AccountSummaryAccountsEditComposite(
				summaryAccountWrapper, SWT.NONE, dirtyStateManager, true);
		
		stackLayout.topControl = normalAccountWrapper;
		
//		pack(true);
	}

	public void setAccount(Account account, boolean isSummaryAccount) {
		editingSummaryAccount = isSummaryAccount;
		if (!isSummaryAccount) {
			stackLayout.topControl = normalAccountWrapper;
			normalSummaryAccountsComp.setAccount(account);
		}
		else {
			stackLayout.topControl = summaryAccountWrapper;
			summarySummedAccountsComp.setSummaryAccount((SummaryAccount)account);
			summarySummaryAccountsComp.setAccount(account);
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				AccountConfigurationComposite.this.layout(true);
			}
		});
	}
	
	/**
	 * returns the currently edited {@link Account}
	 * which was previously set by {@link #setAccount(Account, boolean)}
	 * 
	 * @return the currently edited {@link Account}
	 */
	public Account getAccount() 
	{
		if (!editingSummaryAccount) {
			return normalSummaryAccountsComp.getAccount();
		}
		else {
//			summarySummedAccountsComp.setSummaryAccount((SummaryAccount)account);
			return summarySummaryAccountsComp.getAccount();
		}		
	}	
	
//	public IAccountContainer getAccountContainer() {
//		if (!editingSummaryAccount)
//			return normalSummaryAccountsComp;
//		else
//			return summarySummaryAccountsComp;
//	}
}

