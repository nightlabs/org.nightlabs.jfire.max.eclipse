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

package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.InvoiceDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class InvoiceRootTreeNode extends ArticleContainerRootTreeNode
{
	public InvoiceRootTreeNode(HeaderTreeNode parent, boolean customerSide)
	{
		super(parent, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.InvoiceRootTreeNode.name"), parent.getHeaderTreeComposite().imageInvoiceRootTreeNode, customerSide); //$NON-NLS-1$
		init();
	}

	public static final String[] FETCH_GROUPS_INVOICE = new String[] {
		FetchPlan.DEFAULT,
		Invoice.FETCH_GROUP_CUSTOMER_ID,
		Invoice.FETCH_GROUP_VENDOR_ID //,
//		Invoice.FETCH_GROUP_CURRENCY
	};

	@Override
	@Implement
	protected HeaderTreeNode createArticleContainerNode(byte position, ArticleContainer articleContainer)
	{
		return new InvoiceTreeNode(this, position, (Invoice) articleContainer);
	}

	@Override
	@Implement
	protected List<Object> doLoadChildElements(AnchorID vendorID, AnchorID customerID, long rangeBeginIdx, long rangeEndIdx, ProgressMonitor monitor)
			throws Exception
	{
		return CollectionUtil.castList(
				new InvoiceDAO().getInvoices(vendorID, customerID,
				rangeBeginIdx, rangeEndIdx,
				FETCH_GROUPS_INVOICE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
	}

	@Override
	@Implement
	protected List<ArticleContainer> doLoadNewArticleContainers(Set<ArticleContainerID> articleContainerIDs, ProgressMonitor monitor)
	{
		Set<InvoiceID> invoiceIDs = CollectionUtil.castSet(articleContainerIDs);
		return CollectionUtil.castList(new InvoiceDAO().getInvoices(
				invoiceIDs,
				FETCH_GROUPS_INVOICE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
	}

	@Override
	@Implement
	protected Class<? extends ArticleContainerID> getArticleContainerIDClass()
	{
		return InvoiceID.class;
	}
}
