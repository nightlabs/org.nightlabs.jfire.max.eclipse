/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.security.id.UserID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUICurrentUserMultiple extends AbstractValueProviderGUIUsers {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<Collection<UserID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig, boolean isScheduledReportParameterConfig) {
			return new ValueProviderGUICurrentUserMultiple(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_CURRENT_USER_MULTIPLE;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
	}
	
	private Collection<UserID> currentUserID;

	/**
	 * 
	 */
	public ValueProviderGUICurrentUserMultiple(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
		currentUserID = Collections.singleton(SecurityReflector.getUserDescriptor().getUserObjectID());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createGUI(Composite wrapper) {
		XComposite comp = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		Label label = new Label(comp, SWT.WRAP);
		label.setText(
				String.format(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects.ValueProviderGUICurrentUserMultiple.label.preselectedUser.text"), //$NON-NLS-1$
				currentUserID.iterator().next().userID));
		super.createGUI(comp);
		return comp;
	}
	
	@Override
	protected int getTypeFlags() {
		return UserSearchComposite.FLAG_TYPE_USER;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	@Override
	public Collection<UserID> getOutputValue() {
		Collection<UserID> superValue = super.getOutputValue();
		if (superValue != null)
			return superValue;
		return currentUserID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	@Override
	public boolean isAcquisitionComplete() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setInputParameterValue(String parameterID, Object value) {
	}

}
