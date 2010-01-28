package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery;

import org.eclipse.core.runtime.IExecutableExtension;
import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IScheduledReportDeliveryDelegateEditFactory extends IExecutableExtension {
	
	String getId();
	
	String getName();
	
	IScheduledReportDeliveryDelegateEdit createDeliveryDelegateEdit();
	
	boolean canHandleDeliveryDelegate(IScheduledReportDeliveryDelegate deliveryDelegate);
}
