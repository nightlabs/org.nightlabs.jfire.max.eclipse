package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringOrderTreeNode extends HeaderTreeNode {

	
	private RecurringOrder recurringOrder;	
	
	
	public RecurringOrderTreeNode(HeaderTreeNode parent, byte position ,RecurringOrder recurringOrder) {
		super(parent, position);
		this.recurringOrder = recurringOrder;

	}
	
	

	@Override
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public Image getColumnImage(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return getHeaderTreeComposite().getImageOrderTreeNode();
			default:
				return null;
		}
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0: return recurringOrder.getOrderIDPrefix() + '/' + ObjectIDUtil.longObjectIDFieldToString(recurringOrder.getOrderID());
			default:
				return null;
		}
	}

	
	
	
	
	
	
	@Override
	protected List<Object> loadChildData(ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
