package org.nightlabs.jfire.reporting.admin.parameter;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;


/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueAcquisitionSetupProvider 
implements IValueAcquisitionSetupProvider 
{
	public ValueAcquisitionSetupProvider(ValueAcquisitionSetup setup) {
		this.setup = setup;
	}
	
	private ValueAcquisitionSetup setup;
	public ValueAcquisitionSetup getValueAcquisitionSetup() {
		return setup;
	}
	public void setValueAcquisitionSetup(ValueAcquisitionSetup setup) {
		this.setup = setup;
	}
	
}
