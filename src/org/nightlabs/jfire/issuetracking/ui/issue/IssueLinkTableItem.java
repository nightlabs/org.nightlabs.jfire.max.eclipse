package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.util.Util;

public class IssueLinkTableItem {
	private ObjectID linkedObjectID;
	private IssueLinkType issueLinkType;
	private IssueLink issueLink;

	public IssueLinkTableItem(ObjectID linkObjectID, IssueLinkType issueLinkType) {
		if (linkObjectID == null)
			throw new IllegalArgumentException("linkObjectID == null");

		if (issueLinkType == null)
			throw new IllegalArgumentException("issueLinkType == null");

		this.linkedObjectID = linkObjectID;
		this.issueLinkType = issueLinkType;
	}
	
	public ObjectID getLinkedObjectID() {
		return linkedObjectID;
	}
	
	public IssueLinkType getIssueLinkType() {
		return issueLinkType;
	}

//	public void setLinkObjectID(ObjectID linkedObjectID) {
//	if (linkObjectID == null)
//		throw new IllegalArgumentException("linkObjectID == null");
//		this.linkObjectID = linkedObjectID;
//	}

	public void setIssueLinkType(IssueLinkType issueLinkType) {
		if (issueLinkType == null)
			throw new IllegalArgumentException("issueLinkType == null");

		this.issueLinkType = issueLinkType;
	}

	public void initIssueLink(IssueLink issueLink) {
		if (issueLink == null)
			throw new IllegalArgumentException("issueLink == null");

		if (this.issueLink != null && !this.issueLink.equals(issueLink))
			throw new IllegalStateException("IssueLink already assigned!");

		this.issueLink = issueLink;
	}

	public IssueLink getIssueLink() {
		return issueLink;
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
			Util.equals(this.linkedObjectID, o.linkedObjectID) &&
			Util.equals(this.issueLinkType, o.issueLinkType);
	}

	@Override
	public int hashCode()
	{
		return
			Util.hashCode(this.linkedObjectID) ^
			Util.hashCode(this.issueLinkType);
	}
}
