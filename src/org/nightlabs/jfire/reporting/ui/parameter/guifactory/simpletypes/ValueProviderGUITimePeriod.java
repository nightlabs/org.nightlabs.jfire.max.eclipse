/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.ui.parameter.ValueProviderConfigUtil;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.util.TimePeriod;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUITimePeriod extends AbstractValueProviderGUI<TimePeriod> {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<TimePeriod> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUITimePeriod(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_TIME_PERIOD;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	
	private XComposite wrapper;
	private DateTimeEdit fromEdit;
	private DateTimeEdit toEdit;

	/**
	 * 
	 */
	public ValueProviderGUITimePeriod(ValueProviderConfig valueProviderConfig) {
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
			fromEdit = new DateTimeEdit(
					group, DateFormatter.FLAGS_DATE_LONG_TIME_HM | DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX, 
					Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.guifactory.simpletypes.ValueProviderGUITimePeriod.fromDateTimeEdit.caption")); //$NON-NLS-1$
			fromEdit.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					notifyOutputChanged();
				}
			});
			toEdit = new DateTimeEdit(
					group, DateFormatter.FLAGS_DATE_LONG_TIME_HM | DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX, 
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
	public TimePeriod getOutputValue() {
		TimePeriod period = new TimePeriod();
		if (fromEdit.isActive())
			period.setFrom(fromEdit.getDate());
		else 
			period.setFrom(null);
		if (toEdit.isActive())
			period.setTo(toEdit.getDate());
		else 
			period.setTo(null);
		if (period.isConfining())
			return period;
		else 
			return null;
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
