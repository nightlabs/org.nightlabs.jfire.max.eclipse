package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.email;

import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.AbstractDeliveryDelegateEditFactory;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEdit;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DeliveryDelegateEditFactoryEmail extends AbstractDeliveryDelegateEditFactory {

	/**
	 * 
	 */
	public DeliveryDelegateEditFactoryEmail() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEditFactory#canHandleDeliveryDelegate(org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate)
	 */
	@Override
	public boolean canHandleDeliveryDelegate(IScheduledReportDeliveryDelegate deliveryDelegate) {
		return org.nightlabs.jfire.reporting.scheduled.ScheduledReportDeliveryDelegateEMail.class.isInstance(deliveryDelegate);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEditFactory#createDeliveryDelegateEdit()
	 */
	@Override
	public IScheduledReportDeliveryDelegateEdit createDeliveryDelegateEdit() {
		return new DeliveryDelegateEditEmail();
	}

}
