package org.nightlabs.jfire.trade.transfer.error;

import org.eclipse.swt.graphics.Image;

public abstract class TransferTreeNode
{
	public abstract String getColumnText(int columnIndex);
	public Image getColumnImage(int columnIndex)
	{
		return null;
	}

	public abstract Object[] getChildren();

	public boolean hasChildren()
	{
		Object[] children = getChildren();
		return children != null && children.length > 0;
	}
}
