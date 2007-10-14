/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter;

import org.eclipse.core.runtime.IExecutableExtension;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IValueProviderGUIFactory extends IExecutableExtension {

	public ValueProviderID getValueProviderID();
	
	IValueProviderGUI createValueProviderGUI(ValueProviderConfig valueProviderConfig);
	
}
