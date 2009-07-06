/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.security.id.UserID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIUserGroups extends AbstractValueProviderGUIUsers {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<Collection<UserID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig, boolean isScheduledReportParameterConfig) {
			return new ValueProviderGUIUserGroups(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			throw new UnsupportedOperationException("UserSecurityGroups should not be used in reporting! Marco."); //$NON-NLS-1$
//			return ReportingConstants.VALUE_PROVIDER_ID_USER_GROUPS;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	/**
	 * 
	 */
	public ValueProviderGUIUserGroups(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	@Override
	protected int getTypeFlags() {
		return UserSearchComposite.FLAG_TYPE_ORGANISATION;
	}
}
