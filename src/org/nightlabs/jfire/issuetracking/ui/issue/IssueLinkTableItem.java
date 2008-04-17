package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLinkType;

public class IssueLinkTableItem {
	private ObjectID linkObjectID;
	private IssueLinkType issueLinkType;
	
	public IssueLinkTableItem(ObjectID linkObjectID, IssueLinkType issueLinkType) {
		this.linkObjectID = linkObjectID;
		this.issueLinkType = issueLinkType;
	}
	
	public ObjectID getLinkObjectID() {
		return linkObjectID;
	}
	
	public IssueLinkType getIssueLinkType() {
		return issueLinkType;
	}
	
	public void setLinkObjectID(ObjectID linkObjectID) {
		this.linkObjectID = linkObjectID;
	}
	
	public void setIssueLinkType(IssueLinkType issueLinkType) {
		this.issueLinkType = issueLinkType;
	}
}
