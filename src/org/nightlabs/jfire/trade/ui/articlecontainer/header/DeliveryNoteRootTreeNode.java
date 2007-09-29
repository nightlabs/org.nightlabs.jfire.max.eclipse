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
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.DeliveryNoteDAO;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DeliveryNoteRootTreeNode extends ArticleContainerRootTreeNode
{
	public DeliveryNoteRootTreeNode(HeaderTreeNode headerTreeNode, boolean customerSide)
	{
		super(headerTreeNode, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.DeliveryNoteRootTreeNode.name"), headerTreeNode.getHeaderTreeComposite().imageDeliveryNoteRootTreeNode, customerSide); //$NON-NLS-1$
		init();
	}

	public static final String[] FETCH_GROUPS_DELIVERY_NOTE = new String[] {
		FetchPlan.DEFAULT,
		DeliveryNote.FETCH_GROUP_CUSTOMER_ID,
		DeliveryNote.FETCH_GROUP_VENDOR_ID
	};

	@Implement
	protected HeaderTreeNode createArticleContainerNode(byte position, ArticleContainer articleContainer)
	{
		return new DeliveryNoteTreeNode(this, position, (DeliveryNote) articleContainer);
	}

	@Implement
	protected List<ArticleContainer> doLoadChildElements(AnchorID vendorID, AnchorID customerID, long rangeBeginIdx, long rangeEndIdx, ProgressMonitor monitor)
			throws Exception
	{
		return CollectionUtil.castList(
				new DeliveryNoteDAO().getDeliveryNotes(vendorID, customerID,
				rangeBeginIdx, rangeEndIdx,
				FETCH_GROUPS_DELIVERY_NOTE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
	}

	@Implement
	protected List<ArticleContainer> doLoadNewArticleContainers(Set<ArticleContainerID> articleContainerIDs, ProgressMonitor monitor)
	{
		Set<DeliveryNoteID> deliveryNoteIDs = CollectionUtil.castSet(articleContainerIDs);
		return CollectionUtil.castList(new DeliveryNoteDAO().getDeliveryNotes(
				deliveryNoteIDs,
				FETCH_GROUPS_DELIVERY_NOTE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
	}

	@Implement
	protected Class getArticleContainerIDClass()
	{
		return DeliveryNoteID.class;
	}
}