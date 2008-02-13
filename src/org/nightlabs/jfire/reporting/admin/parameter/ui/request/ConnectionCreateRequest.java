package org.nightlabs.jfire.reporting.admin.parameter.ui.request;

import org.eclipse.gef.requests.CreateConnectionRequest;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConnectionCreateRequest
//extends CreateRequest
extends CreateConnectionRequest
{
	private ValueAcquisitionSetup valueAcquisitionSetup;

	public ValueAcquisitionSetup getValueAcquisitionSetup() {
		return valueAcquisitionSetup;
	}

	public void setValueAcquisitionSetup(ValueAcquisitionSetup valueAcquisitionSetup) {
		this.valueAcquisitionSetup = valueAcquisitionSetup;
	}
	

}
