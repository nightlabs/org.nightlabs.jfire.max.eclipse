package org.nightlabs.jfire.reporting.admin.parameter.ui;

import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface IValueAcquisitionSetupProvider
{
	ValueAcquisitionSetup getValueAcquisitionSetup();
	void setValueAcquisitionSetup(ValueAcquisitionSetup setup);
}
