package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.List;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.progress.ProgressMonitor;

public class RecurringSaleRootTreeNode extends HeaderTreeNode.RootNode{

	private HeaderTreeNode[] _children;
	private RecurringOrderRootTreeNode recurringOrderTreeNode;
	
	public 	RecurringSaleRootTreeNode(HeaderTreeNode parent)
	{
		super(parent, "Sale",parent.getHeaderTreeComposite().getImageCustomerRootTreeNode());
		
		recurringOrderTreeNode = new RecurringOrderRootTreeNode(this, true);

		_children = new HeaderTreeNode[] {
				recurringOrderTreeNode
		};
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
