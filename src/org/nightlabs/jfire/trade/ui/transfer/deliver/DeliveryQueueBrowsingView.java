package org.nightlabs.jfire.trade.ui.transfer.deliver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;

public class DeliveryQueueBrowsingView
extends LSDViewPart
{
	public static final String ID_VIEW = DeliveryQueueBrowsingView.class.getName();

	private DeliveryQueueBrowsingComposite comp;
//	private Label loginLabel;

	public void createPartContents(Composite parent) {
		comp = new DeliveryQueueBrowsingComposite(parent, SWT.NONE);
	}

	void refreshContent() {
		comp.refreshDeliveryQueues();
	}

	void deliverCheckedDeliveries() {
		comp.deliverCheckedDeliveries();
	}
	
	void checkAllDeliveries() {
		comp.checkAllDeliveries();
	}
	
	void uncheckAllDeliveries() {
		comp.uncheckAllDeliveries();
	}
}