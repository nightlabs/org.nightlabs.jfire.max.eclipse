/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IValueProviderGUI<OutputType> {

	Control createGUI(Composite wrapper);
	
	boolean isAcquisitionComplete();
	
	void setInputParameterValue(String parameterID, Object value);
	
	OutputType getOutputValue();
	
	void addValueProviderGUIListener(IValueProviderGUIListener listener);
	void removeValueProviderGUIListener(IValueProviderGUIListener listener);
}
