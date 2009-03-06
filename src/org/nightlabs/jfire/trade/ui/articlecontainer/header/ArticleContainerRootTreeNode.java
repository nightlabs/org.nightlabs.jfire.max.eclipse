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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class ArticleContainerRootTreeNode extends HeaderTreeNode.RootNode
{
	private static final Logger logger = Logger.getLogger(ArticleContainerRootTreeNode.class);

	protected static int rangeLength = 3; // TODO This should come from a JDO-ConfigModule

	private boolean purchaseMode;
	private boolean endCustomerMode;

	public ArticleContainerRootTreeNode(HeaderTreeNode parent, String name, Image image, boolean purchaseMode, boolean endCustomerMode)
	{
		super(parent, name, image);
		this.purchaseMode = purchaseMode;
		this.endCustomerMode = endCustomerMode;
	}

	private int nextRangeBeginIdx = 0;

	@Override
	public void clear()
	{
		super.clear();
		articleContainerIDsLoaded.clear();
		nextRangeBeginIdx = 0;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#loadChildData(IProgressMonitor)
	 */
	@Override
	protected List<Object> loadChildData(ProgressMonitor monitor)
	{
		try {
			AnchorID vendorID = getHeaderTreeComposite().getMyOrganisationLegalEntityID();
			AnchorID customerID = getHeaderTreeComposite().getPartnerID();
			if (purchaseMode) {
				AnchorID tmp = vendorID;
				vendorID = customerID;
				customerID = tmp;
			}

			if (vendorID == null || customerID == null) {
				logger.warn("loadChildData: vendorID or customerID) undefined! vendorID=\""+vendorID+"\" customerID=\""+customerID+"\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return new ArrayList<Object>();
			}

			return doLoadChildElements(vendorID, customerID, nextRangeBeginIdx, nextRangeBeginIdx += rangeLength, monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract List<Object> doLoadChildElements(AnchorID vendorID, AnchorID customerID, long rangeBeginIdx, long rangeEndIdx, ProgressMonitor monitor)
	throws Exception;

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#createChildNodes(java.util.List)
	 */
	@Override
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData)
	{
		ArrayList<HeaderTreeNode> res = new ArrayList<HeaderTreeNode>();
		for (Iterator<Object> it = childData.iterator(); it.hasNext(); ) {
			ArticleContainer articleContainer = (ArticleContainer) it.next();
			ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(articleContainer);
			synchronized(articleContainerIDsLoaded) {
				if (!articleContainerIDsLoaded.contains(articleContainerID)) {
					articleContainerIDsLoaded.add(articleContainerID);
					res.add(createArticleContainerNode(POSITION_LAST_CHILD, articleContainer));
				}
			}
		}
		createMoreNode(childData, res);
		return res;
	}

	protected abstract HeaderTreeNode createArticleContainerNode(
			byte position, ArticleContainer articleContainer);

	protected void createMoreNode(List<Object> childData, List<HeaderTreeNode> childNodes)
	{
		if (childData.size() < rangeLength)
			return;

		SimpleNode moreNode = new SimpleNode(this, POSITION_LAST_CHILD, "...", true) { //$NON-NLS-1$
			@Override
			public List<Object> loadChildData(ProgressMonitor monitor)
			{
				return ArticleContainerRootTreeNode.this.loadChildData(monitor);
			}
			@Override
			public List<HeaderTreeNode> createChildNodes(List<Object> childData)
			{
				ArticleContainerRootTreeNode.this.removeChildNode(this);
				ArticleContainerRootTreeNode.this.createChildNodes(childData);
				return null;
			}
		};
		childNodes.add(moreNode);
	}

	public boolean isPurchaseMode()
	{
		return purchaseMode;
	}

	public boolean isEndCustomerMode() {
		return endCustomerMode;
	}

//	protected abstract Class<? extends ArticleContainerID> getArticleContainerIDClass();

	protected abstract boolean acceptNewArticleContainer(Object newObjectID);

	private Set<ArticleContainerID> articleContainerIDsLoaded = new HashSet<ArticleContainerID>();

	protected abstract List<ArticleContainer> doLoadNewArticleContainers(
			Set<ArticleContainerID> articleContainerIDs, ProgressMonitor monitor);

	@Override
	public Collection<DirtyObjectID> onNewElementsCreated(Collection<DirtyObjectID> dirtyObjectIDs, ProgressMonitor monitor)
	{
		if (children != null) {
			Map<Object, DirtyObjectID> objectID2DirtyObjectIDMap = new HashMap<Object, DirtyObjectID>(dirtyObjectIDs.size());

			Set<ArticleContainerID> articleContainerIDsToLoad = new HashSet<ArticleContainerID>();
			for (Iterator<DirtyObjectID> itDirtyObjectID = dirtyObjectIDs.iterator(); itDirtyObjectID.hasNext(); ) {
				DirtyObjectID dirtyObjectID = itDirtyObjectID.next();
				objectID2DirtyObjectIDMap.put(dirtyObjectID.getObjectID(), dirtyObjectID);
				if (acceptNewArticleContainer(dirtyObjectID.getObjectID())) {
					itDirtyObjectID.remove();
					synchronized(articleContainerIDsLoaded) {
						if (!articleContainerIDsLoaded.contains(dirtyObjectID.getObjectID()))
							articleContainerIDsToLoad.add((ArticleContainerID) dirtyObjectID.getObjectID());
					}
				}
			}

			if (!articleContainerIDsToLoad.isEmpty()) {

				final List<ArticleContainer> articleContainers = doLoadNewArticleContainers(articleContainerIDsToLoad, monitor);

				for (Iterator<ArticleContainer> it = articleContainers.iterator(); it.hasNext(); ) {
					ArticleContainer articleContainer = it.next();
					AnchorID vendorID = purchaseMode ? getHeaderTreeComposite().getPartnerID() : getHeaderTreeComposite().getMyOrganisationLegalEntityID();
					if (!articleContainer.getVendorID().equals(vendorID)) {
						it.remove();
						dirtyObjectIDs.add(objectID2DirtyObjectIDMap.get(JDOHelper.getObjectId(articleContainer)));
					}
				}

				// TODO we should sort the ArticleContainers!
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (children == null)
							return;

						for (ArticleContainer articleContainer : articleContainers) {
							ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(articleContainer);
							synchronized(articleContainerIDsLoaded) {
								if (!articleContainerIDsLoaded.contains(articleContainerID)) {
									articleContainerIDsLoaded.add(articleContainerID);
									++nextRangeBeginIdx;
									createArticleContainerNode(POSITION_FIRST_CHILD, articleContainer);
								}
							}
						}
					}
				});

			} // if (!articleContainerIDsToLoad.isEmpty()) {
		} // if (children != null) {
		return super.onNewElementsCreated(dirtyObjectIDs, monitor);
	}
}
