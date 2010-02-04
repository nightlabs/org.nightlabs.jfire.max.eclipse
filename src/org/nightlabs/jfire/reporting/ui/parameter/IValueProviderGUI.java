/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;

/**
 * Instances of {@link IValueProviderGUI} serve to create UI that lets the user enter
 * parts of the parameters of a report. The instances are created by {@link IValueProviderGUIFactory}s.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IValueProviderGUI<ValueType> {
	
	/**
	 * Create the UI for this {@link IValueProviderGUI}.
	 * 
	 * @param parent The parent Composite where to add the UI to.
	 * @return The newly created {@link Control}.
	 */
	Control createGUI(Composite parent);
	
	/**
	 * Set the initial value of for this object after its UI has been created.
	 *  
	 * @param initalValue The initial value to display.
	 */
	void setInitialValue(ValueType initalValue);
	
	/**
	 * Whether the acquisition of the value(s) that this provider GUI queries from 
	 * the user is finished, i.e. the parameter acquisition process can proceed 
	 * (the outputvalue of this might be the input-value of the following ones).
	 *   
	 * @return Whether the acquisition of this value is finished.
	 */
	boolean isAcquisitionComplete();
	
	/**
	 * Sets the input value with the given parameterID. This is the output-value
	 * from the preceding {@link IValueProviderGUI}s.
	 *   
	 * @param parameterID The parameterID of the input-parameter of the {@link ValueProvider} this is for.
	 * @param value The value to set.
	 */
	void setInputParameterValue(String parameterID, Object value);
	
	/**
	 * Get the value of this {@link IValueProviderGUI}.
	 * This is only valid if {@link #isAcquisitionComplete()} is <code>true</code>.
	 * @return The value of this {@link IValueProviderGUI}.
	 */
	ValueType getOutputValue();
	
	void addValueProviderGUIListener(IValueProviderGUIListener listener);
	void removeValueProviderGUIListener(IValueProviderGUIListener listener);
}
 