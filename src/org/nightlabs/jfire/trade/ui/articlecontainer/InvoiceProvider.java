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

package org.nightlabs.jfire.trade.ui.articlecontainer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.accounting.query.InvoiceQuery;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider;
import org.nightlabs.jfire.base.ui.login.Login;

public class InvoiceProvider extends JDOObjectProvider
{
	private static InvoiceProvider _sharedInstance;
	public static InvoiceProvider sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new InvoiceProvider();

		return _sharedInstance;
	}

	public InvoiceProvider() { }

	public Invoice getInvoice(InvoiceID orderID, String[] fetchGroups, int maxFetchDepth)
	{
		return getInvoice(null, orderID, fetchGroups, maxFetchDepth);
	}

	private AccountingManager accountingManager;

	public Invoice getInvoice(AccountingManager tradeManager, InvoiceID invoiceID, String[] fetchGroups, int maxFetchDepth)
	{
		this.accountingManager = tradeManager;
		return (Invoice) getJDOObject(null, invoiceID, fetchGroups, maxFetchDepth);
	}

	@Override
	protected Object retrieveJDOObject(String scope, Object objectID, String[] fetchGroups, int maxFetchDepth)
	throws Exception
	{
		if (accountingManager == null)
			accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

		try {
			return accountingManager.getInvoice((InvoiceID)objectID, fetchGroups, maxFetchDepth);
		} finally {
			accountingManager = null;
		}
	}

	public List<Invoice> getInvoicesByInvoiceQueries(Collection<InvoiceQuery> invoiceQueries, String[] fetchGroups, int maxFetchDepth)
	{
		try {
			this.accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			Set<InvoiceID> invoiceIDs = accountingManager.getInvoiceIDs(invoiceQueries);
			return getInvoices(invoiceIDs, fetchGroups, maxFetchDepth);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			this.accountingManager = null;
		}
	}

	public List<Invoice> getInvoices(Collection<InvoiceID> invoiceIDs, String[] fetchGroups, int maxFetchDepth)
	{
		return (List<Invoice>) getJDOObjects(null, invoiceIDs, fetchGroups, maxFetchDepth);
	}

	@Override
	protected Collection retrieveJDOObjects(String scope, Set objectIDs, String[] fetchGroups, int maxFetchDepth)
			throws Exception
	{
		return accountingManager.getInvoices(objectIDs, fetchGroups, maxFetchDepth);
	}
}
