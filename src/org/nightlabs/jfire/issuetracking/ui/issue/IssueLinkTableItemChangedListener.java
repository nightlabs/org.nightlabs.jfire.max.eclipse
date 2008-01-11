package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangedEvent;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkSelectionChangedEvent;

public interface IssueLinkTableItemChangedListener {
	void issueLinkItemChanged(IssueLinkItemChangedEvent itemChangedEvent);
}
