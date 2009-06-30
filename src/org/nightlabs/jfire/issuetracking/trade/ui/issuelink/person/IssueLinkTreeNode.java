package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.eclipse.swt.graphics.Image;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public abstract class IssueLinkTreeNode{

	protected String name;
	protected Image icon;

	public IssueLinkTreeNode(String name, Image icon)
	{
		super();
		this.name = name;
		this.icon = icon;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}


	public abstract Object[] getChildNodes();
	public abstract boolean hasChildren();

}
