/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderConfigUtil {

	public static String getValueProviderMessage(ValueProviderConfig valueProviderConfig) {
		if (valueProviderConfig.getMessage() == null || valueProviderConfig.getMessage().isEmpty()) {
//			ValueProviderID providerID = ValueProviderID.create(
//					valueProviderConfig.getValueProviderOrganisationID(), valueProviderConfig.getValueProviderCategoryID(), valueProviderConfig.getValueProviderID()
//				);
			ValueProviderID providerID = valueProviderConfig.getConfigValueProviderID();
			ValueProvider provider = ValueProviderDAO.sharedInstance().getValueProvider(
					providerID, new String[] {FetchPlan.DEFAULT, ValueProvider.FETCH_GROUP_DEFAULT_MESSAGE}, new NullProgressMonitor()
				);
			if (!provider.getDefaultMessage().isEmpty())
				return provider.getDefaultMessage().getText();
			else
				return ""; //$NON-NLS-1$
		}
		else
			return valueProviderConfig.getMessage().getText();
	}
}
