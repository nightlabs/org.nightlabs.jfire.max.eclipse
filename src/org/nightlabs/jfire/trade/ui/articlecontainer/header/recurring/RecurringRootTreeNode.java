package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.List;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;



/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringRootTreeNode
extends HeaderTreeNode.RootNode
{
	private HeaderTreeNode[] _children;

	private RecurringSaleRootTreeNode recurringSaleRootTreeNode;
	private RecurringPurchaseRootTreeNode recurringPurchaseRootTreeNode;

	public RecurringRootTreeNode(HeaderTreeComposite headerTreeComposite)
	{
		super(headerTreeComposite, "Recurring Trade", headerTreeComposite.getImageVendorRootTreeNode()); //$NON-NLS-1$

		recurringSaleRootTreeNode = new RecurringSaleRootTreeNode(this);
		recurringPurchaseRootTreeNode = new RecurringPurchaseRootTreeNode(this);

		_children = new HeaderTreeNode[] {
				recurringSaleRootTreeNode,
				recurringPurchaseRootTreeNode
		};

		children = CollectionUtil.array2ArrayList(_children, true);
	}

	@Override
	public HeaderTreeNode[] getChildren()
	{
		return _children;
	}

	@Override
	@Implement
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

	@Override
	@Implement
	protected List<Object> loadChildData(ProgressMonitor monitor)
	{
		throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
	}

}
