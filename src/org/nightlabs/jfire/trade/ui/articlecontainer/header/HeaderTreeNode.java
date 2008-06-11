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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class HeaderTreeNode
{
	public static abstract class RootNode extends HeaderTreeNode
	{
		private String name;
		private Image image;

		public RootNode(HeaderTreeComposite headerTreeComposite, String name, Image image)
		{
			this(null, headerTreeComposite, name, image);
		}
		public RootNode(HeaderTreeNode parent, String name, Image image)
		{
			this(parent, null, name, image);
		}
		protected RootNode(HeaderTreeNode parent, HeaderTreeComposite headerTreeComposite, String name, Image image)
		{
			super(parent, POSITION_FIRST_CHILD);
			if (parent == null && headerTreeComposite == null)
				throw new IllegalArgumentException("parent == null && headerTreeComposite == null"); //$NON-NLS-1$

			if (parent != null)
				headerTreeComposite = parent.getHeaderTreeComposite();

			if (headerTreeComposite == null)
				throw new NullPointerException("headerTreeComposite"); //$NON-NLS-1$

			if (parent == null)
				this.setHeaderTreeComposite(headerTreeComposite);

			this.name = name;
			if (name == null)
				throw new NullPointerException("name"); //$NON-NLS-1$

			this.image = image;
		}

		/**
		 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#getColumnText(int)
		 */
		@Override
		public String getColumnText(int columnIndex)
		{
			if (columnIndex == 0)
				return name;

			return ""; //$NON-NLS-1$
		}

		@Override
		public Image getColumnImage(int columnIndex)
		{
			switch (columnIndex) {
				case 0:
					return image;
				default:
					return null;
			}
		}
	}

	private HeaderTreeComposite headerTreeComposite;
	private HeaderTreeNode parent;

	public static final byte POSITION_FIRST_CHILD = 1;
	public static final byte POSITION_LAST_CHILD = 2;

	private byte position;

	/**
	 * @param parent <code>null</code> or the parent node.
	 */
	public HeaderTreeNode(HeaderTreeNode parent, byte position)
	{
		this.parent = parent;
		this.position = position;
		if (parent != null)
			this.headerTreeComposite = parent.headerTreeComposite;
	}

	/**
	 * Inheritants of this class MUST call this method at the end of their constructor!
	 */
	protected void init()
	{
		if (parent != null)
			parent.addChildNode(this, position);
	}

	protected List<HeaderTreeNode> children = null;

	/**
	 * Adds a childnode, if the children list is initialized (means the children have already
	 * been loaded from the server). If the children list is <tt>null</tt>, nothing is done.
	 * <p>
	 * This method is called automatically by the constructor when creating a new node. No
	 * need to call it manually!
	 *
	 * @param childNode
	 */
	protected void addChildNode(HeaderTreeNode childNode, byte position)
	{
		if (childNode.getParent() != this)
			throw new IllegalArgumentException("childNode.getParent() != this!"); //$NON-NLS-1$

		if (children == null)
			return;

		if (POSITION_FIRST_CHILD == position)
			children.add(0, childNode);
		else if (POSITION_LAST_CHILD == position)
			children.add(childNode);
		else
			throw new IllegalArgumentException("position is invalid!"); //$NON-NLS-1$

		refresh();
	}

	protected void removeChildNode(HeaderTreeNode childNode)
	{
		if (childNode.getParent() != this)
			throw new IllegalArgumentException("childNode.getParent() != this!"); //$NON-NLS-1$

		if (children == null)
			return;

		children.remove(childNode);
		refresh();
	}

	/**
	 * This method removes the children to force a reload if queried later.
	 */
	public void clear()
	{
		children = null;
	}

	/**
	 * @return Returns the headerTreeComposite.
	 */
	public HeaderTreeComposite getHeaderTreeComposite()
	{
		return headerTreeComposite;
	}
	/**
	 * @param headerTreeComposite The headerTreeComposite to set.
	 */
	protected void setHeaderTreeComposite(HeaderTreeComposite headerTreeComposite)
	{
		this.headerTreeComposite = headerTreeComposite;
	}
	/**
	 * @return Returns the parent.
	 */
	public HeaderTreeNode getParent()
	{
		return parent;
	}

	public Image getColumnImage(int columnIndex)
	{
		return null;
	}

	public abstract String getColumnText(int columnIndex);

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		if (children == null)
			return true;
		else
			return !children.isEmpty();
	}

	/**
	 * You must implement this method and load the data from the server.
	 * <p>
	 * Because loading data is assumed to be expensive, this method is
	 * called ASYNCHRONOUSLY on a worker thread (via a Job)!
	 * </p>
	 * @param monitor A fresh monitor which you should use for tracking your activities.
	 *
	 * @return Returns a <tt>List</tt> of <tt>Object</tt>. These objects
	 *		are passed to {@link #createChildNodes(List)} afterwards.
	 */
	protected abstract List<Object> loadChildData(ProgressMonitor monitor);

	/**
	 * This method is called on the SWT GUI thread. You must implement this
	 * method and create the child-nodes for the data you've loaded from
	 * the server before in {@link #loadChildData(ProgressMonitor)}.
	 *
	 * @param childData The result of the method {@link #loadChildData(ProgressMonitor)}.
	 * @return Returns instances of <code>HeaderTreeNode</code>.
	 */
	protected abstract List<HeaderTreeNode> createChildNodes(List<Object> childData);

	private Job currentJob = null;

	/**
	 * This method is called by {@link HeaderTreeContentProvider#getChildren(Object)}.
	 */
	public HeaderTreeNode[] getChildren()
	{
		if (children == null) {
			SimpleNode loadingDataNode = new SimpleNode(this, POSITION_LAST_CHILD, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode.loadingDataNode.name"), false); //$NON-NLS-1$
			children = new ArrayList<HeaderTreeNode>(1);
			children.add(loadingDataNode);

			currentJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode.loadJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor)
				{
					final List<Object> c = loadChildData(monitor);

					final Job thisJob = this;
					Display.getDefault().asyncExec(new Runnable() {
						public void run()
						{
							if (currentJob != thisJob)
								return;

							if (headerTreeComposite.isDisposed())
								return;

							children = null; // avoids that the constructor of the new children adds the children to the parent (which would be slower)
							children = createChildNodes(c);
							if (children == null)
								children = new ArrayList<HeaderTreeNode>(0);
							refresh();
						}
					});

					monitor.done();
					return Status.OK_STATUS;
				}
			};
			currentJob.schedule();
		}

		return CollectionUtil.collection2TypedArray(children, HeaderTreeNode.class, false);
	}

	public void refresh()
	{
		headerTreeComposite.getHeaderTreeViewer().refresh(this);
	}

	public void select()
	{
		headerTreeComposite.getHeaderTreeViewer().setSelection(new StructuredSelection(this), true);
	}

	public void expandToLevel(int level)
	{
		headerTreeComposite.getHeaderTreeViewer().expandToLevel(this, level);
	}

	public void collapseToLevel(int level)
	{
		headerTreeComposite.getHeaderTreeViewer().collapseToLevel(this, level);
	}

	public boolean isExpanded()
	{
		return headerTreeComposite.getHeaderTreeViewer().getExpandedState(this);
	}

	public Collection<DirtyObjectID> onNewElementsCreated(Collection<DirtyObjectID> dirtyObjectIDs, ProgressMonitor monitor)
	{
		if (children == null)
			return dirtyObjectIDs;

		for (HeaderTreeNode node : children) {
			dirtyObjectIDs = node.onNewElementsCreated(dirtyObjectIDs, monitor);
			if (dirtyObjectIDs == null || dirtyObjectIDs.isEmpty())
				return dirtyObjectIDs;
		}
		monitor.done();
		return dirtyObjectIDs;
	}
}
