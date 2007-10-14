/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter;

import java.util.Map;

import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IReportParameterController {
	void setParameterValue(String parameterID, Object value);
	Map<ValueProviderID, Object> getProviderValues();
}
