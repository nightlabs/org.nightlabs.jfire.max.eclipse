/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter.guifactory.simpletypes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.parameter.ValueProviderConfigUtil;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIDouble extends AbstractValueProviderGUI<Double> {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUIDouble(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_DOUBLE;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	
	private LabeledText labeledText;

	/**
	 * 
	 */
	public ValueProviderGUIDouble(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		if (labeledText == null) {
			labeledText = new LabeledText(wrapper, ValueProviderConfigUtil.getValueProviderMessage(getValueProviderConfig()));
			labeledText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					notifyOutputChanged();
				}
			});
		}
		return labeledText;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Double getOutputValue() {
		return Double.parseDouble(labeledText.getText());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return labeledText.getText().length() > 0 || getValueProviderConfig().isAllowNullOutputValue();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {		
	}

}
