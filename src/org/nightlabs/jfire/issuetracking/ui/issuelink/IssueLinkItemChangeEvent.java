package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Collection;
import java.util.EventObject;

import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;

public class IssueLinkItemChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public static enum ChangeType {
		add,
		remove
	}

	private ChangeType changeType;
	private Collection<IssueLinkTableItem> issueLinkTableItems;

	public IssueLinkItemChangeEvent(Object source, ChangeType changeType, Collection<IssueLinkTableItem> issueLinkTableItems) {
		super(source);
		assert changeType != null : "changeType != null";
		assert issueLinkTableItems != null : "issueLinkTableItems != null";
		this.changeType = changeType;
		this.issueLinkTableItems = issueLinkTableItems;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public Collection<IssueLinkTableItem> getIssueLinkTableItems() {
		return issueLinkTableItems;
	}
}
