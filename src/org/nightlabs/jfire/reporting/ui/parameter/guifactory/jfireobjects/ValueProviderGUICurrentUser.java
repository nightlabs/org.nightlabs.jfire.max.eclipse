/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import javax.jdo.JDOHelper;

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
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.security.id.UserID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUICurrentUser extends AbstractValueProviderGUI<UserID> {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<UserID> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUICurrentUser(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_USER;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	
	private UserID currentUserID;
	private UserSearchComposite searchComposite;

	/**
	 * 
	 */
	public ValueProviderGUICurrentUser(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
		currentUserID = SecurityReflector.sharedInstance().getUserDescriptor().getUserObjectID();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		XComposite comp = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		Label label = new Label(comp, SWT.WRAP);
		label.setText(
				String.format("The current user '%s' is preselected, but you can select another one.", 
				currentUserID.userID));
		searchComposite = new UserSearchComposite(comp, SWT.NONE, UserSearchComposite.FLAG_TYPE_USER | UserSearchComposite.FLAG_SEARCH_BUTTON);
		return searchComposite;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public UserID getOutputValue() {
		if (searchComposite.getSelectedUser() != null)
			return (UserID) JDOHelper.getObjectId(searchComposite.getSelectedUser());
		return currentUserID;
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
