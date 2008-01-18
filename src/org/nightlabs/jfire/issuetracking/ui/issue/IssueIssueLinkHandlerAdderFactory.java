package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueIssueLinkHandlerAdderFactory 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder() {
		IssueLinkAdder adder = new IssueIssueLinkAdder();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkObjectClass() {
		return Issue.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return new IssueIssueLinkHandler();
	}
}
