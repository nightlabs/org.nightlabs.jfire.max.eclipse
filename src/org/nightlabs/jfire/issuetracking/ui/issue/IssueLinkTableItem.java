package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.util.Util;

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

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (!(obj instanceof IssueLinkTableItem))
			return false;

		IssueLinkTableItem o = (IssueLinkTableItem) obj;

		return
			Util.equals(this.linkObjectID, o.linkObjectID) &&
			Util.equals(this.issueLinkType, o.issueLinkType);
	}

	@Override
	public int hashCode()
	{
		return
			Util.hashCode(this.linkObjectID) ^
			Util.hashCode(this.issueLinkType);
	}
}
