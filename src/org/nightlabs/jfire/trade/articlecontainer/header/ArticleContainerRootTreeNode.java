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
import org.nightlabs.annotation.Implement;
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

	private boolean purchase;

	public ArticleContainerRootTreeNode(HeaderTreeNode parent, String name, Image image, boolean purchase)
	{
		super(parent, name, image);
		this.purchase = purchase;
	}

	private int nextRangeBeginIdx = 0;

	public void clear()
	{
		super.clear();
		articleContainerIDsLoaded.clear();
		nextRangeBeginIdx = 0;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.header.HeaderTreeNode#loadChildData(IProgressMonitor)
	 */
	@Implement
	protected List loadChildData(ProgressMonitor monitor)
	{
		try {
			AnchorID vendorID = getHeaderTreeComposite().getMyOrganisationLegalEntityID();
			AnchorID customerID = getHeaderTreeComposite().getPartnerID();
			if (purchase) {
				AnchorID tmp = vendorID;
				vendorID = customerID;
				customerID = tmp;
			}

			if (vendorID == null || customerID == null) {
				logger.warn("loadChildData: vendorID or customerID undefined! vendorID=\""+vendorID+"\" customerID=\""+customerID+"\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return new ArrayList<ArticleContainer>();
			}

			return doLoadChildElements(vendorID, customerID, nextRangeBeginIdx, nextRangeBeginIdx += rangeLength, monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract List<ArticleContainer> doLoadChildElements(AnchorID vendorID, AnchorID customerID, long rangeBeginIdx, long rangeEndIdx, ProgressMonitor monitor)
	throws Exception;

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.header.HeaderTreeNode#createChildNodes(java.util.List)
	 */
	@Implement
	protected List<HeaderTreeNode> createChildNodes(List childData)
	{
		ArrayList<HeaderTreeNode> res = new ArrayList<HeaderTreeNode>();
		for (Iterator it = childData.iterator(); it.hasNext(); ) {
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

	protected void createMoreNode(List childData, List childNodes)
	{
		if (childData.size() < rangeLength)
			return;

		SimpleNode moreNode = new SimpleNode(this, POSITION_LAST_CHILD, "...", true) { //$NON-NLS-1$
			public List loadChildData(ProgressMonitor monitor)
			{
				return ArticleContainerRootTreeNode.this.loadChildData(monitor);
			}
			public List<HeaderTreeNode> createChildNodes(List childData)
			{
				ArticleContainerRootTreeNode.this.removeChildNode(this);
				ArticleContainerRootTreeNode.this.createChildNodes(childData);
				return null;
			}
		};
		childNodes.add(moreNode);
	}

	public boolean isPurchase()
	{
		return purchase;
	}

	protected abstract Class getArticleContainerIDClass();

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
				if (getArticleContainerIDClass().isInstance(dirtyObjectID.getObjectID())) {
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
					AnchorID vendorID = purchase ? getHeaderTreeComposite().getPartnerID() : getHeaderTreeComposite().getMyOrganisationLegalEntityID();
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
