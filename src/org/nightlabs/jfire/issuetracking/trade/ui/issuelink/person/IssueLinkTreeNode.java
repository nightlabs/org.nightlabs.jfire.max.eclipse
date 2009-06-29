package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.eclipse.swt.graphics.Image;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public abstract class IssueLinkTreeNode{

	// Some comments to this code (I already refactored it):
	//
	// 1) Why is "hasChildNodes" a Boolean instead of a boolean?! That might lead to NPEs!
	// 2) Why was this class not abstract when its methods are overridden anyway?
	// 3) Why was only one of the method overridden (getChildNodes) assuming that it is called before "getHasChildNodes".
	//    It is nowhere documented that these methods are called in that order and might be called in a different order
	//    leading to wrong results!
	// 4) See notes in LegalEntityPersonIssueLinkTreeView!
	//
	// Marco.

	protected String name;
	protected Image icon;
//	protected Boolean hasChildNodes; // Why the hell is this a *B*oolean instead of *b*oolean?!?!???! And why is it necessary at all?????

	public IssueLinkTreeNode(String name, Image icon)
	{
		super();
		this.name = name;
		this.icon = icon;
	}

//	public Boolean getHasChildNodes() {
//		return hasChildNodes;
//	}
//
//	public void setHasChildNodes(Boolean hasChildren) {
//		this.hasChildNodes = hasChildren;
//	}

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
//	{ // WTF does this non-used implementation here?
//		return new ArrayList<IssueLinkTreeNode>().toArray();
//	}

	public abstract boolean hasChildren();

}
