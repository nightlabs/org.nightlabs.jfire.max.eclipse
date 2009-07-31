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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.recurring.RecurringTradeManagerRemote;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class CreateOfferAction extends CreateArticleContainerAction
{
	private HeaderTreeComposite headerTreeComposite;

	private static final ImageDescriptor IMAGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articleContainer/createOffer16.gif"); //$NON-NLS-1$

	public CreateOfferAction(HeaderTreeComposite headerTreeComposite)
	{
		super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOfferAction.text"), IMAGE_DESCRIPTOR); //$NON-NLS-1$
		this.headerTreeComposite = headerTreeComposite;
	}

	private void createOffer(OrderID orderID, boolean recurring)
	throws Exception
	{
		Offer offer = null;
		// FIXME IDPREFIX (null-parameter for create*Offer() methods) should be asked from user if necessary!
		String offerIDPrefix = null;
		if (recurring) {
			RecurringTradeManagerRemote rtm = JFireEjb3Factory.getRemoteBean(RecurringTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			offer = rtm.createRecurringOffer(orderID, offerIDPrefix, null, 1);
		} else {
			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			offer = tradeManager.createOffer(orderID, offerIDPrefix, null, 1);
		}

		if (offer != null) {
			final OfferID offerID = (OfferID) JDOHelper.getObjectId(offer);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					HeaderTreeComposite.openEditor(new ArticleContainerEditorInput(offerID));
				}
			});
		}
	}

	@Override
	public void run()
	{
		final CreateOrderAction.CreateOrderJob createOrderJob = headerTreeComposite.getCreateOrderAction().newCreateOrderJob();
		createOrderJob.setOpenOrderEditor(false);
		Job createOfferJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOfferAction.job.creatingOrder")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				HeaderTreeNode selectedNode = headerTreeComposite.getSelectedNode();
				HeaderTreeNode rootTreeNode = getRootTreeNode(selectedNode);
				if (rootTreeNode instanceof EndCustomerRootTreeNode)
					return Status.CANCEL_STATUS;

				if (selectedNode instanceof HeaderTreeNode.ArticleContainerNode) {
					HeaderTreeNode.ArticleContainerNode orderTreeNode = (HeaderTreeNode.ArticleContainerNode) headerTreeComposite.getSelectedNode();
					if (orderTreeNode.getArticleContainer() instanceof Offer) {
						if (!(orderTreeNode.getParent() instanceof HeaderTreeNode.ArticleContainerNode))
							throw new IllegalStateException("Why the hell is the parent of an offer-tree-node, not an ArticleContainerNode?!?");

						orderTreeNode = (HeaderTreeNode.ArticleContainerNode)orderTreeNode.getParent();
						if (!(orderTreeNode.getArticleContainer() instanceof Order))
							throw new IllegalStateException("Why the hell is the parent of an offer-tree-node, not an order?!?");
					}

					OrderID orderID = (OrderID) JDOHelper.getObjectId(orderTreeNode.getArticleContainer());
					boolean recurring = orderTreeNode.getArticleContainer() instanceof RecurringOrder;
					createOffer(orderID, recurring);
				}
				else {
					if (!createOrderJob.isRunnable())
						return Status.CANCEL_STATUS;

					createOrderJob.run(new NullProgressMonitor()); // TODO real progress monitor
					OrderID orderID = createOrderJob.getOrderID();
					if (orderID == null)
					 return Status.CANCEL_STATUS;

					createOffer(orderID, createOrderJob.isRecurring());
				}

				return Status.OK_STATUS;
			}
		};
		createOfferJob.setUser(true);
		createOfferJob.setPriority(Job.INTERACTIVE);
		createOfferJob.schedule();
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
			headerTreeView.getHeaderTreeComposite().getCreateOfferAction().calculateEnabled(selection);
			action.setEnabled(
					headerTreeView.getHeaderTreeComposite().getCreateOfferAction().isEnabled()
			);
		}
	}
}
