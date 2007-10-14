/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.deliverynote.report;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class ValueProviderGUIFactoryDeliveryNoteByCustomer 
implements IValueProviderGUIFactory 
{
	public IValueProviderGUI createValueProviderGUI(
			ValueProviderConfig valueProviderConfig) 
	{
		return new ValueProviderGUIDeliveryNoteByCustomer(valueProviderConfig);
	}

	public ValueProviderID getValueProviderID() {
		return ReportingTradeConstants.VALUE_PROVIDER_ID_TRADE_DOCUMENTS_DELIVERY_NOTE_BY_CUSTOMER;
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {

	}

}
