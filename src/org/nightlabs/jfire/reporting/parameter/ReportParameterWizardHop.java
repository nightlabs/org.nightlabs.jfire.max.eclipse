/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportParameterWizardHop extends WizardHop implements IReportParameterController {

	private Map<String, Object> parameters = new HashMap<String, Object>();
	private Map<ValueProviderID, Object> providerValues = new HashMap<ValueProviderID, Object>();
	
	public ReportParameterWizardHop(IWizardHopPage entryPage) {
		super(entryPage);
	}

	public ReportParameterWizardHop() {
		super();
	}
	
	public void setParameterValue(String parameterID, Object value) {
		parameters.put(parameterID, value);
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public Map<ValueProviderID, Object> getProviderValues() {
		return providerValues;
	}

}
