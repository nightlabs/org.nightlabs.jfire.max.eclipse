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

package org.nightlabs.jfire.trade.articlecontainer.header;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.trade.notification.ArticleContainerLifecycleListenerFilter;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class HeaderTreeContentProvider
implements ITreeContentProvider
{
	private static final Logger logger = Logger.getLogger(HeaderTreeContentProvider.class);

	private SaleRootTreeNode saleRootTreeNode;
	private PurchaseRootTreeNode purchaseRootTreeNode;

	private HeaderTreeNode.RootNode[] rootNodes;
	private HeaderTreeComposite headerTreeComposite;

	public HeaderTreeContentProvider(HeaderTreeComposite headerTreeComposite)
	{
		this.headerTreeComposite = headerTreeComposite;
		saleRootTreeNode = new SaleRootTreeNode(headerTreeComposite);
		purchaseRootTreeNode = new PurchaseRootTreeNode(headerTreeComposite);

		rootNodes = new HeaderTreeNode.RootNode[] {
			saleRootTreeNode,
			purchaseRootTreeNode
		};

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

		jdoLifecycleListener = new JDOLifecycleAdapterJob(Messages.getString("org.nightlabs.jfire.trade.articlecontainer.header.HeaderTreeContentProvider.loadJob.name")) { //$NON-NLS-1$
			private ArticleContainerLifecycleListenerFilter filter = new ArticleContainerLifecycleListenerFilter(
					new JDOLifecycleState[] { JDOLifecycleState.NEW },
					headerTreeComposite.getPartnerID());

			public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
			{
				return filter;
			}
			public void notify(JDOLifecycleEvent event)
			{
				Collection<DirtyObjectID> dirtyObjectIDs = new ArrayList<DirtyObjectID>(event.getDirtyObjectIDs());
				for (HeaderTreeNode node : rootNodes) {
					dirtyObjectIDs = node.onNewElementsCreated(dirtyObjectIDs, getProgressMontitorWrapper());
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

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof HeaderTreeNode)
			return ((HeaderTreeNode)parentElement).getChildren();
		else
			return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof HeaderTreeNode)
			return ((HeaderTreeNode)element).getParent();
		else
			return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof HeaderTreeNode)
			return ((HeaderTreeNode)element).hasChildren();
		else
			return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof HeaderTreeNode)
			return ((HeaderTreeNode)inputElement).getChildren();

		return rootNodes;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		unregisterAndRegisterListeners(false);
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}
