/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.timepattern.input.InputTimePatternEditComposite;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.ValueProviderConfigUtil;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.timepattern.InputTimePatternPeriod;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUITimePeriodScheduled extends AbstractValueProviderGUI<InputTimePatternPeriod> {
	
	private XComposite wrapper;
	private InputTimePatternEditComposite fromEdit;
	private InputTimePatternEditComposite toEdit;

	/**
	 * 
	 */
	public ValueProviderGUITimePeriodScheduled(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite parent) {
		if (wrapper == null) {
			wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			wrapper.getGridData().verticalAlignment = GridData.BEGINNING;
			wrapper.getGridData().grabExcessVerticalSpace = false;
			Group group = new Group(wrapper, SWT.NONE);
			group.setLayout(new GridLayout(2, true));
			group.setLayoutData(new GridData(GridData.FILL_BOTH));
			group.setText(ValueProviderConfigUtil.getValueProviderMessage(getValueProviderConfig()));
			fromEdit = new InputTimePatternEditComposite(
					group, SWT.NONE | InputTimePatternEditComposite.STYLE_SHOW_ACTIVE_CHECKBOX,
					Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes.ValueProviderGUITimePeriod.fromDateTimeEdit.caption")); //$NON-NLS-1$
			fromEdit.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					notifyOutputChanged();
				}
			});			
			toEdit = new InputTimePatternEditComposite(
					group, SWT.NONE | InputTimePatternEditComposite.STYLE_SHOW_ACTIVE_CHECKBOX,
					Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes.ValueProviderGUITimePeriod.toDateTimeEdit.caption")); //$NON-NLS-1$
			toEdit.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					notifyOutputChanged();
				}
			});
		}
		return wrapper;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public InputTimePatternPeriod getOutputValue() {
		InputTimePatternPeriod period = new InputTimePatternPeriod();
		if (fromEdit.isActive())
			period.setFrom(fromEdit.getInputTimePattern());
		else
			period.setFrom(null);
		if (toEdit.isActive())
			period.setTo(toEdit.getInputTimePattern());
		else
			period.setTo(null);
		if (period.isConfining())
			return period;
		else
			return null;
	}
	
	@Override
	public void setInitialValue(InputTimePatternPeriod initalValue) {
		fromEdit.setInputTimePattern(initalValue.getFrom());
		fromEdit.setActive(initalValue.isFromSet());
		toEdit.setInputTimePattern(initalValue.getTo());
		toEdit.setActive(initalValue.isToSet());
		super.setInitialValue(initalValue);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return getOutputValue() != null || getValueProviderConfig().isAllowNullOutputValue();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {
	}

}
