package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueInvoiceLinkHandlerAdderFactory 
extends AbstractIssueLinkHandlerFactory
{
	public IssueLinkAdder createIssueLinkAdder() {
		IssueLinkAdder adder = new IssueInvoiceLinkAdder();
		adder.init(this);
		
		return adder;
	}

	public Class<? extends Object> getLinkObjectClass() {
		return Invoice.class;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		return null;
	}
}
