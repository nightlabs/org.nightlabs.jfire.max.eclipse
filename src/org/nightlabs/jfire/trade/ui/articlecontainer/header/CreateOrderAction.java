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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.GeneralEditorInputOrder;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreateOrderAction extends Action
{
	private HeaderTreeComposite headerTreeComposite;

	// TODO should not be static and should be obtained by our new SharedImages registry
	private static final ImageDescriptor IMAGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articleContainer/createOrder16.gif"); //$NON-NLS-1$

	public CreateOrderAction(HeaderTreeComposite headerTreeComposite)
	{
		super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOrderAction.text"), IMAGE_DESCRIPTOR); //$NON-NLS-1$
		this.headerTreeComposite = headerTreeComposite;
	}

	@Override
	public void run()
	{
		Job createOrderJob = new Job("Creating order...") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				try {
					TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//					TODO where do we get the currency from? User prefs?
					AnchorID customerID = (AnchorID) JDOHelper.getObjectId(headerTreeComposite.getPartner());
//					FIXME IDPREFIX (next line) should be asked from user if necessary!

					HeaderTreeNode selectedNode  = (HeaderTreeNode)headerTreeComposite.getSelectedNode();
					// define a sale order 
					boolean SaleOrder = true;

					while(selectedNode != null)
					{
						if(selectedNode instanceof PurchaseRootTreeNode)
							SaleOrder = false; //purchase order
						selectedNode = (HeaderTreeNode) selectedNode.getParent();
					}

					final Order order;

					if(SaleOrder)
						order= tm.createSaleOrder(
								customerID, null, CurrencyID.create("EUR"), //$NON-NLS-1$
								new SegmentTypeID[] {null}, // null here is a shortcut for default segment type
								OrderRootTreeNode.FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					else
						order= tm.createPurchaseOrder(
								customerID, null, CurrencyID.create("EUR"), //$NON-NLS-1$
								new SegmentTypeID[] {null}, // null here is a shortcut for default segment type
								OrderRootTreeNode.FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

//					OrderID orderID = (OrderID) JDOHelper.getObjectId(order);
//					tm.createSegment(orderID, null, null);

//					OrderRootTreeNode orderRootTreeNode = headerTreeComposite.getHeaderTreeContentProvider().getVendorOrderRootTreeNode();
//					OrderTreeNode orderTreeNode = new OrderTreeNode(orderRootTreeNode, OrderTreeNode.POSITION_FIRST_CHILD, order);
//					orderTreeNode.select();

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							HeaderTreeComposite.openEditor(new GeneralEditorInputOrder((OrderID)JDOHelper.getObjectId(order)));
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}

				return Status.OK_STATUS;
			}
		};
		createOrderJob.schedule();
	}

	public static class CreateOrderViewActionDelegate implements IViewActionDelegate
	{
		private HeaderTreeView headerTreeView;

		public void init(IViewPart view)
		{
			headerTreeView = (HeaderTreeView) view;
		}

		public void run(IAction action)
		{
			headerTreeView.getHeaderTreeComposite().getCreateOrderAction().run();
		}

		public void selectionChanged(IAction action, ISelection selection) { }
	}
}
