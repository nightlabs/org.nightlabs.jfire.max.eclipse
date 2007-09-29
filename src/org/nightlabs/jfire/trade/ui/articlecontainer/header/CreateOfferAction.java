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

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.GeneralEditorInputOffer;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class CreateOfferAction extends Action
{
	private HeaderTreeComposite headerTreeComposite;

	private static final ImageDescriptor IMAGE_DESCRIPTOR = TradePlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articleContainer/createOffer16.gif"); //$NON-NLS-1$

	public CreateOfferAction(HeaderTreeComposite headerTreeComposite)
	{
		super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOfferAction.text"), IMAGE_DESCRIPTOR); //$NON-NLS-1$
		this.headerTreeComposite = headerTreeComposite;
	}

	public void run()
	{
		try {
			OrderTreeNode orderTreeNode = (OrderTreeNode) headerTreeComposite.getSelectedNode();
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//		 FIXME IDPREFIX (next line) should be asked from user if necessary!
//			Offer offer = tradeManager.createOffer(
//					(OrderID) JDOHelper.getObjectId(orderTreeNode.getOrder()), null,
//					OrderTreeNode.FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			OfferTreeNode offerTreeNode = new OfferTreeNode(orderTreeNode, OrderTreeNode.POSITION_FIRST_CHILD, offer);
//			offerTreeNode.select();

			Offer offer = tradeManager.createOffer(
					(OrderID) JDOHelper.getObjectId(orderTreeNode.getOrder()), null,
					null, 1);

			HeaderTreeComposite.openEditor(new GeneralEditorInputOffer((OfferID)JDOHelper.getObjectId(offer)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class CreateOfferViewActionDelegate implements IViewActionDelegate
	{
		private HeaderTreeView headerTreeView;

		public void init(IViewPart view)
		{
			headerTreeView = (HeaderTreeView) view;
		}

		public void run(IAction action)
		{
			headerTreeView.getHeaderTreeComposite().getCreateOfferAction().run();
		}

		public void selectionChanged(IAction action, ISelection selection)
		{
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (sel.isEmpty())
				action.setEnabled(false);
			else {
				Object firstElement = sel.getFirstElement();
				action.setEnabled(firstElement instanceof OrderID);
			}
		}
	}
}
