package org.nightlabs.jfire.trade.transfer.deliver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

public class DeliveryQueueBrowsingView extends LSDViewPart {

	DeliveryQueueBrowsingComposite comp;
	Label loginLabel;
	
	public void createPartContents(Composite parent) {
		comp = new DeliveryQueueBrowsingComposite(parent, SWT.NONE);
	}
	
	void refreshContent() {
		comp.refreshDeliveryQueues();
	}
	
	void deliverCheckedDeliveries() {
		comp.deliverCheckedDeliveries();
	}
}