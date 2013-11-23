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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.trade.notification.ArticleContainerLifecycleListenerFilter;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring.RecurringRootTreeNode;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class HeaderTreeContentProvider
implements IHeaderTreeContentProvider
{
	private static final Logger logger = Logger.getLogger(HeaderTreeContentProvider.class);

	private HeaderTreeNode.RootNode[] rootNodes;
	private HeaderTreeComposite headerTreeComposite;

	public HeaderTreeContentProvider(HeaderTreeComposite headerTreeComposite)
	{
		this.headerTreeComposite = headerTreeComposite;
		SaleRootTreeNode saleRootTreeNode = new SaleRootTreeNode(headerTreeComposite);
		EndCustomerRootTreeNode endCustomerRootTreeNode = new EndCustomerRootTreeNode(headerTreeComposite);
		PurchaseRootTreeNode purchaseRootTreeNode = new PurchaseRootTreeNode(headerTreeComposite);
		RecurringRootTreeNode recurringRootTreeNode = new RecurringRootTreeNode(headerTreeComposite);

		rootNodes = new HeaderTreeNode.RootNode[] {
			saleRootTreeNode,
			endCustomerRootTreeNode,
			purchaseRootTreeNode,
			recurringRootTreeNode
		};

		unregisterAndRegisterListeners(true);
	}

	public HeaderTreeContentProvider(HeaderTreeComposite headerTreeComposite, HeaderTreeNode.RootNode[] rootNodes)
	{
		this.headerTreeComposite = headerTreeComposite;
		this.rootNodes = rootNodes;
		unregisterAndRegisterListeners(true);
	}

	private void unregisterAndRegisterListeners(boolean registerAfterUnregister)
	{
		if (jdoLifecycleListener != null) {
			JDOLifecycleManager.sharedInstance().removeLifecycleListener(jdoLifecycleListener);
			jdoLifecycleListener = null;
		}

		if (!registerAfterUnregister)
			return;

		jdoLifecycleListener = new JDOLifecycleAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeContentProvider.loadJob.name")) { //$NON-NLS-1$
			private ArticleContainerLifecycleListenerFilter filter = new ArticleContainerLifecycleListenerFilter(
					new JDOLifecycleState[] { JDOLifecycleState.NEW },
					headerTreeComposite.getPartnerID());
			@Override
			public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
			{
				return filter;
			}
			@Override
			public void notify(JDOLifecycleEvent event)
			{
				Collection<DirtyObjectID> dirtyObjectIDs = new ArrayList<DirtyObjectID>(event.getDirtyObjectIDs());
				for (HeaderTreeNode node : rootNodes) {
					dirtyObjectIDs = node.onNewElementsCreated(dirtyObjectIDs, getProgressMonitor());
					if (dirtyObjectIDs == null || dirtyObjectIDs.isEmpty())
						break;
				}
			}
		};
		JDOLifecycleManager.sharedInstance().addLifecycleListener(jdoLifecycleListener);
	}

	private JDOLifecycleListener jdoLifecycleListener;

	public void clear()
	{
		for (int i = 0; i < rootNodes.length; ++i) {
			for (HeaderTreeNode node : rootNodes[i].getChildren())
				node.clear();
		}
		unregisterAndRegisterListeners(true);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof HeaderTreeNode)
			return ((HeaderTreeNode)parentElement).getChildren();
		else
			return null;
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof HeaderTreeNode)
			return ((HeaderTreeNode)element).getParent();
		else
			return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof HeaderTreeNode)
			return ((HeaderTreeNode)element).hasChildren();
		else
			return false;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof HeaderTreeNode)
			return ((HeaderTreeNode)inputElement).getChildren();

		return rootNodes;
	}

	@Override
	public void dispose()
	{
		unregisterAndRegisterListeners(false);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}
