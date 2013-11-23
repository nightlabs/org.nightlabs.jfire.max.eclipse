package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.trade.Order;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkHandlerAdderFactoryOrder 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder(Issue issue) {
		IssueLinkAdder adder = new IssueLinkAdderOrder();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkedObjectClass() {
		return Order.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return new IssueLinkHandlerOrder();
	}
}
