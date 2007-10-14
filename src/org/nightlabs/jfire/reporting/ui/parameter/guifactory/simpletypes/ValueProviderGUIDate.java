/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes;

import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.ui.parameter.ValueProviderConfigUtil;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIDate extends AbstractValueProviderGUI<Date> {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUIDate(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_DATE;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	
	private DateTimeEdit dateTimeEdit;

	
	public ValueProviderGUIDate(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		if (dateTimeEdit == null) {
			dateTimeEdit = new DateTimeEdit(wrapper, DateFormatter.FLAGS_DATE_LONG_TIME_HM, ValueProviderConfigUtil.getValueProviderMessage(getValueProviderConfig()));
		}
		return dateTimeEdit;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Date getOutputValue() {
		return dateTimeEdit.getDate();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {		
	}

}
