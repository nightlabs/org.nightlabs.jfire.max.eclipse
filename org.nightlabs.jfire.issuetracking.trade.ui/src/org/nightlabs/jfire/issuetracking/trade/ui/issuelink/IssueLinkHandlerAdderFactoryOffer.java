package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.trade.Offer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkHandlerAdderFactoryOffer 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder(Issue issue) {
		IssueLinkAdder adder = new IssueLinkAdderOffer();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkedObjectClass() {
		return Offer.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return new IssueLinkHandlerOffer();
	}
}
