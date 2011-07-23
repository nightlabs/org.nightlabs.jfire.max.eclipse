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

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.config.TradeConfigModule;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.trade.recurring.RecurringTradeManagerRemote;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring.RecurringSaleRootTreeNode;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreateOrderAction extends CreateArticleContainerAction
{
	private HeaderTreeComposite headerTreeComposite;

	// TODO should not be static and should be obtained by our new SharedImages registry
	private static final ImageDescriptor IMAGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articleContainer/createOrder16.gif"); //$NON-NLS-1$

	public CreateOrderAction(HeaderTreeComposite headerTreeComposite)
	{
		super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOrderAction.text"), IMAGE_DESCRIPTOR); //$NON-NLS-1$
		this.headerTreeComposite = headerTreeComposite;
	}

	public CreateOrderJob newCreateOrderJob()
	{
		return new CreateOrderJob();
	}

	public class CreateOrderJob extends Job
	{
		private AnchorID customerID;
		private boolean saleOrder = false;
		private boolean recurring = false;

		private boolean openOrderEditor = true;

		private boolean runnable = true;

		private OrderID orderID = null; // will be set as soon as it's created.

		public CreateOrderJob() {
			super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOrderAction.job.creatingOrder"));
			customerID = (AnchorID) JDOHelper.getObjectId(headerTreeComposite.getPartner());

			HeaderTreeNode selectedNode  = headerTreeComposite.getSelectedNode();
			if (selectedNode == null)
				runnable = false;
			if (
					selectedNode.getParent() == null &&                      // is root node
					(
						!(selectedNode instanceof SaleRootTreeNode) &&       // but no normal sale
						!(selectedNode instanceof PurchaseRootTreeNode)      // nor normal purchase node
					)
				) {
				// can't determinde sale/purchase mode, do nothing
				runnable = false;
			}

			// define a sale order
			saleOrder = false;
			recurring = false;
			HeaderTreeNode checkNode = selectedNode;
			while (checkNode != null) {
				recurring = recurring || checkNode instanceof RecurringSaleRootTreeNode;
				saleOrder = saleOrder || checkNode instanceof RecurringSaleRootTreeNode || checkNode instanceof SaleRootTreeNode;
				checkNode = checkNode.getParent();
			}

			setPriority(Job.INTERACTIVE);
			setUser(true);
		}

		public boolean isRunnable() {
			return runnable;
		}

		@Override
		protected IStatus run(ProgressMonitor monitor) throws Exception
		{
			if (!runnable)
				return Status.CANCEL_STATUS;

			monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOrderAction.task.creatingOrder"), 100); //$NON-NLS-1$
			try {
				TradeConfigModule tradeConfigModule = ConfigUtil.getUserCfMod(
						TradeConfigModule.class,
						new String[] {
							FetchPlan.DEFAULT,
							TradeConfigModule.FETCH_GROUP_CURRENCY,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 30)
				);

				TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());

				Order order = null;

//				FIXME IDPREFIX should be asked from user if necessary!
				if(recurring)
				{
					if (saleOrder) {
						RecurringTradeManagerRemote rtm = JFireEjb3Factory.getRemoteBean(RecurringTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());

						order = rtm.createSaleRecurringOrder(customerID, null,
								tradeConfigModule.getCurrencyID(), new SegmentTypeID[] { null },
								null,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					}
					// purchaseOrders not yet supported for recurring trade
				}
				else
				{
					if(saleOrder)
						order = tm.createSaleOrder(
								customerID, null, tradeConfigModule.getCurrencyID(),
								new SegmentTypeID[] {null}, // null here is a shortcut for default segment type
								null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					else
						order = tm.createPurchaseOrder(
								customerID, null, tradeConfigModule.getCurrencyID(),
								new SegmentTypeID[] {null}, // null here is a shortcut for default segment type
								null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				}

				monitor.worked(70);

//				OrderID orderID = (OrderID) JDOHelper.getObjectId(order);
//				tm.createSegment(orderID, null, null);

//				OrderRootTreeNode orderRootTreeNode = headerTreeComposite.getHeaderTreeContentProvider().getVendorOrderRootTreeNode();
//				OrderTreeNode orderTreeNode = new OrderTreeNode(orderRootTreeNode, OrderTreeNode.POSITION_FIRST_CHILD, order);
//				orderTreeNode.select();
				orderID = (OrderID) (order == null ? null : JDOHelper.getObjectId(order));
				if (isOpenOrderEditor() && orderID != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							HeaderTreeComposite.openEditor(new ArticleContainerEditorInput(orderID));
						}
					});
				}
			} catch (Exception x) {
				throw new RuntimeException(x);
			} finally {
				monitor.done();
			}

			return Status.OK_STATUS;
		}

		public boolean isOpenOrderEditor() {
			return openOrderEditor;
		}
		public void setOpenOrderEditor(boolean openOrderEditor) {
			this.openOrderEditor = openOrderEditor;
		}

		public OrderID getOrderID() {
			return orderID;
		}

		public boolean isRecurring() {
			return recurring;
		}
	}

	@Override
	public void run()
	{
		CreateOrderJob createOrderJob = newCreateOrderJob();
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

		public void selectionChanged(IAction action, ISelection selection) 
		{
			HeaderTreeComposite headerTreeComposite = headerTreeView.getHeaderTreeComposite();
			if (headerTreeComposite != null && !headerTreeComposite.isDisposed() && headerTreeComposite.getCreateOrderAction() != null) {
				headerTreeComposite.getCreateOrderAction().calculateEnabled(selection);
				action.setEnabled(headerTreeComposite.getCreateOrderAction().isEnabled());				
			}
		}
	}
}
