package org.nightlabs.jfire.reporting.admin.parameter.ui.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueAcquistionSetupPropertySource
extends AbstractPropertySource
{

	public ValueAcquistionSetupPropertySource(ValueAcquisitionSetup valueAcquisitionSetup) {
		super();
		this.valueAcquisitionSetup = valueAcquisitionSetup;
	}

	private ValueAcquisitionSetup valueAcquisitionSetup;
	
	public Object getEditableValue() {
		return valueAcquisitionSetup;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return null;
	}

	public Object getPropertyValue(Object arg0) {
		return null;
	}

	public void setPropertyValue(Object arg0, Object arg1) {

	}

}
