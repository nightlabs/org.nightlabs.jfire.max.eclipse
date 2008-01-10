package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.trade.Order;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueOrderLinkHandlerAdderFactory 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder() {
		IssueLinkAdder adder = new IssueOrderLinkAdder();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkObjectClass() {
		return Order.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return new IssueOrderLinkHandler();
	}
}
