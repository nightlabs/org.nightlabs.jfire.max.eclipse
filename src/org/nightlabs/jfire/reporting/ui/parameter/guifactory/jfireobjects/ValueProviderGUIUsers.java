/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.Collection;

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
public class ValueProviderGUIUsers extends AbstractValueProviderGUIUsers {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<Collection<UserID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUIUsers(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_USERS;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	/**
	 * 
	 */
	public ValueProviderGUIUsers(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	@Override
	protected int getTypeFlags() {
		return UserSearchComposite.FLAG_TYPE_USER;
	}
}
