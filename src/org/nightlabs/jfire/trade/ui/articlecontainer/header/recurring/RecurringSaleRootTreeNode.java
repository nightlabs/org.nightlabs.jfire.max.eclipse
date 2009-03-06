package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.Collection;
import java.util.List;

import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class RecurringSaleRootTreeNode extends HeaderTreeNode.RootNode{

	private HeaderTreeNode[] _children;
	private RecurringOrderRootTreeNode recurringOrderTreeNode;

	public 	RecurringSaleRootTreeNode(HeaderTreeNode parent)
	{
		super(parent, "Sale", parent.getHeaderTreeComposite().getImageCustomerRootTreeNode());

		recurringOrderTreeNode = new RecurringOrderRootTreeNode(this, false, false);

		_children = new HeaderTreeNode[] {
				recurringOrderTreeNode
		};

		// This sub-class of HeaderTreeNode does not call init() as this would start
		// a job to load the children.
	}

	/**
	 * Delegates to the children
	 */
	@Override
	public void clear() {
//		super.clear();
		for (HeaderTreeNode child : _children) {
			child.clear();
		}
	}

	/**
	 * Delegates to the children.
	 */
	@Override
	public Collection<DirtyObjectID> onNewElementsCreated(
			Collection<DirtyObjectID> dirtyObjectIDs, ProgressMonitor monitor) {
//		super.onNewElementsCreated(dirtyObjectIDs, monitor);
		if (_children == null)
			return dirtyObjectIDs;

		for (HeaderTreeNode node : _children) {
			dirtyObjectIDs = node.onNewElementsCreated(dirtyObjectIDs, monitor);
			if (dirtyObjectIDs == null || dirtyObjectIDs.isEmpty())
				return dirtyObjectIDs;
		}
		monitor.done();
		return dirtyObjectIDs;
	}

	@Override
	public HeaderTreeNode[] getChildren()
	{
		return _children;
	}

	@Override
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

	@Override
	protected List<Object> loadChildData(ProgressMonitor monitor)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

}
