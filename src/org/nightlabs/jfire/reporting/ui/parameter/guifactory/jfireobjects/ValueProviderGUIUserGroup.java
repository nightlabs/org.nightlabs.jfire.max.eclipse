/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.security.id.UserID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIUserGroup extends AbstractValueProviderGUIUser {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<UserID> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUIUserGroup(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_USER_GROUP;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	/**
	 * 
	 */
	public ValueProviderGUIUserGroup(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}
	
	@Override
	protected int getTypeFlags() {
		return UserSearchComposite.FLAG_TYPE_USER_GROUP;
	}
}
