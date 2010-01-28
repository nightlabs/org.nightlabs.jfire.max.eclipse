package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IScheduledReportDeliveryDelegateEdit {

	Control createControl(Composite parent);
	
	void setDeliveryDelegate(IScheduledReportDeliveryDelegate deliveryDelegate);
	
	void setDirtyStateManager(IDirtyStateManager dirtyStateManager);
	
	void clear();
	
	IScheduledReportDeliveryDelegate getScheduledReportDeliveryDelegate();
}
