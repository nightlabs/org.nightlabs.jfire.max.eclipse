package org.nightlabs.jfire.reporting.admin.parameter.ui.request;

import org.eclipse.gef.requests.CreateRequest;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderCreateRequest
extends CreateRequest
{

	public ValueProviderCreateRequest() {
	}

	private ValueProvider valueProvider;
	public ValueProvider getValueProvider() {
		return valueProvider;
	}
	public void setValueProvider(ValueProvider valueProvider) {
		this.valueProvider = valueProvider;
	}
	
	private ValueAcquisitionSetup valueAcquisitionSetup;
	public ValueAcquisitionSetup getValueAcquisitionSetup() {
		return valueAcquisitionSetup;
	}
	public void setValueAcquisitionSetup(ValueAcquisitionSetup valueAcquisitionSetup) {
		this.valueAcquisitionSetup = valueAcquisitionSetup;
	}
	
}
