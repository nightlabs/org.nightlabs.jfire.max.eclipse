package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.trade.Offer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkHandlerAdderFactory 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder() {
		IssueLinkAdder adder = new org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink.IssueLinkAdder();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkObjectClass() {
		return Offer.class;
	}
}
