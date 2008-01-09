package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Collection;

public interface IssueLinkWizardContainer {
	Collection<String> getIssueLinkObjectIds();
	void setIssueLinkObjectIds(Collection<String> ids);
}
