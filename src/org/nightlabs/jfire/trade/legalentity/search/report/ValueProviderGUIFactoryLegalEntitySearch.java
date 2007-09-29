/**
 * 
 */
package org.nightlabs.jfire.trade.legalentity.search.report;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.reporting.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIFactoryLegalEntitySearch 
implements IValueProviderGUIFactory 
{

	public ValueProviderGUIFactoryLegalEntitySearch() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUIFactory#createValueProviderGUI()
	 */
	public IValueProviderGUI createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
		return new ValueProviderGUILegalEntitySearch(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUIFactory#getValueProviderID()
	 */
	public ValueProviderID getValueProviderID() {
		return ReportingTradeConstants.VALUE_PROVIDER_ID_LEGAL_ENTITY_SEARCH;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement arg0, String arg1, Object arg2) throws CoreException {
	}

}
