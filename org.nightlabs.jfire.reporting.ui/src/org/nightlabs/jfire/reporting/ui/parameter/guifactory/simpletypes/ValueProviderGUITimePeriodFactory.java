package org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;

public class ValueProviderGUITimePeriodFactory implements IValueProviderGUIFactory {

	public IValueProviderGUI<? extends Object> createValueProviderGUI(ValueProviderConfig valueProviderConfig, boolean isScheduledReportParameterConfig) {
		if (isScheduledReportParameterConfig) {
			return new ValueProviderGUITimePeriodScheduled(valueProviderConfig);
		} else {
			return new ValueProviderGUITimePeriod(valueProviderConfig);
		}
	}

	public ValueProviderID getValueProviderID() {
		return ReportingConstants.VALUE_PROVIDER_ID_TIME_PERIOD;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
	}
	
}