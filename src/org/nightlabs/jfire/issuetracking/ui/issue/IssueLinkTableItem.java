package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueLinkTableItem {
	private ObjectID linkedObjectID;
	private IssueLinkType issueLinkType;
	private IssueLink issueLink;

	public IssueLinkTableItem(ObjectID linkedObjectID, IssueLinkType issueLinkType) {
		if (linkedObjectID == null)
			throw new IllegalArgumentException("linkedObjectID == null"); //$NON-NLS-1$

		if (issueLinkType == null)
			throw new IllegalArgumentException("issueLinkType == null"); //$NON-NLS-1$

		this.linkedObjectID = linkedObjectID;
		this.issueLinkType = issueLinkType;
	}
	
	public ObjectID getLinkedObjectID() {
		return linkedObjectID;
	}
	
	public IssueLinkType getIssueLinkType() {
		return issueLinkType;
	}

//	public void setLinkedObjectID(ObjectID linkedObjectID) {
//	if (linkedObjectID == null)
//		throw new IllegalArgumentException("linkedObjectID == null");
//		this.linkedObjectID = linkedObjectID;
//	}

	public void setIssueLinkType(IssueLinkType issueLinkType) {
		if (issueLinkType == null)
			throw new IllegalArgumentException("issueLinkType == null"); //$NON-NLS-1$

		this.issueLinkType = issueLinkType;
	}

	public void initIssueLink(IssueLink issueLink) {
		if (issueLink == null)
			throw new IllegalArgumentException("issueLink == null"); //$NON-NLS-1$

		if (this.issueLink != null && !this.issueLink.equals(issueLink))
			throw new IllegalStateException("IssueLink already assigned!"); //$NON-NLS-1$

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
