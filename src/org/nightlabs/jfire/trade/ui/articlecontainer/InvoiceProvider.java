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

import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.jdo.BaseJDOObjectDAO;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class InvoiceProvider
	extends BaseJDOObjectDAO<InvoiceID, Invoice>
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
		return getJDOObject(null, orderID, fetchGroups, maxFetchDepth, new NullProgressMonitor());
	}

	public List<Invoice> getInvoicesByInvoiceQueries(
		QueryCollection<Invoice, ? extends InvoiceQuickSearchQuery> invoiceQueries, 
		String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		try
		{
			AccountingManager accountingManager =
				AccountingManagerUtil.getHome(SecurityReflector.getInitialContextProperties()).create();
			
			Set<InvoiceID> invoiceIDs = accountingManager.getInvoiceIDs(invoiceQueries);
			return getJDOObjects(null, invoiceIDs, fetchGroups, maxFetchDepth, monitor);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<Invoice> getInvoices(Collection<InvoiceID> invoiceIDs, String[] fetchGroups,
		int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObjects(null, invoiceIDs, fetchGroups, maxFetchDepth, monitor);
	}

	@Override
	protected Collection<Invoice> retrieveJDOObjects(Set<InvoiceID> invoiceIDs, String[] fetchGroups,
		int maxFetchDepth, ProgressMonitor monitor) throws Exception
	{
		try
		{
			AccountingManager accountingManager =
				AccountingManagerUtil.getHome(SecurityReflector.getInitialContextProperties()).create();
			
			return accountingManager.getInvoices(invoiceIDs, fetchGroups, maxFetchDepth);
		}
		catch (Exception e) {
			throw new RuntimeException("Problems fetching Invoices:", e);
		}
	}
}
