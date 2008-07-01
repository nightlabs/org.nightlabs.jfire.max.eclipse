package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkHandlerAdderFactoryIssue 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder(Issue issue) {
		IssueLinkAdder adder = new IssueLinkAdderIssue(issue);
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkedObjectClass() {
		return Issue.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return new IssueLinkHandlerIssue();
	}
}
