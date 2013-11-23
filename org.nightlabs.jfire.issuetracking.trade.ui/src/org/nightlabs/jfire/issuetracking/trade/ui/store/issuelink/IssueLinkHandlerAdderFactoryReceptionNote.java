package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.store.ReceptionNote;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkHandlerAdderFactoryReceptionNote 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder(Issue issue) {
		IssueLinkAdder adder = new IssueLinkAdderReceptionNote();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkedObjectClass() {
		return ReceptionNote.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return null;
	}
}
