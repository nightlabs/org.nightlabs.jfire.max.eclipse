/**
 *
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.timepattern.input.InputTimePatternEditComposite;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.ValueProviderConfigUtil;
import org.nightlabs.timepattern.InputTimePattern;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIDateScheduled extends AbstractValueProviderGUI<InputTimePattern> {

	private XComposite wrapper;
	private InputTimePatternEditComposite inputTimePatternComposite;


	public ValueProviderGUIDateScheduled(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite parent) {
		if (inputTimePatternComposite == null) {
			wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			inputTimePatternComposite = new InputTimePatternEditComposite(wrapper, SWT.NONE, ValueProviderConfigUtil
					.getValueProviderMessage(getValueProviderConfig()));
		}
		return wrapper;
	}
	
	@Override
	public void setInitialValue(InputTimePattern initalValue) {
		if (inputTimePatternComposite != null) {
			inputTimePatternComposite.setInputTimePattern(initalValue);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public InputTimePattern getOutputValue() {
		return inputTimePatternComposite.getInputTimePattern();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return inputTimePatternComposite == null
				|| (inputTimePatternComposite != null && inputTimePatternComposite.getInputTimePattern() != null);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {
	}

}
