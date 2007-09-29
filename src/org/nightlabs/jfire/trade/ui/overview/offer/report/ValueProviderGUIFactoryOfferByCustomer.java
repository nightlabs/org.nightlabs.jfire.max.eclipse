/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.offer.report;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.reporting.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class ValueProviderGUIFactoryOfferByCustomer 
implements IValueProviderGUIFactory 
{
	public IValueProviderGUI createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
		return new ValueProviderGUIOfferByCustomer(valueProviderConfig);
	}

	public ValueProviderID getValueProviderID() {
		return ReportingTradeConstants.VALUE_PROVIDER_ID_TRADE_DOCUMENTS_OFFER_BY_CUSTOMER;
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) 
	throws CoreException 
	{

	}

}
