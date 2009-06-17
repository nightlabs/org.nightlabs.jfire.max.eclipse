package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class IssueLinkTreeNode{
		
	protected String name;
	protected Image icon; 
	protected Boolean hasChildNodes; 

	public IssueLinkTreeNode(String name,Image icon, Boolean hasChildren) 
	{
		super();
		this.name = name;
		this.icon = icon;
		this.hasChildNodes = hasChildren;;
	}	
	
	public Boolean getHasChildNodes() {
		return hasChildNodes;
	}

	public void setHasChildNodes(Boolean hasChildren) {
		this.hasChildNodes = hasChildren;
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
	
	public Object[] getChildNodes() {
		return new ArrayList<IssueLinkTreeNode>().toArray();	
	}
	
	
}
