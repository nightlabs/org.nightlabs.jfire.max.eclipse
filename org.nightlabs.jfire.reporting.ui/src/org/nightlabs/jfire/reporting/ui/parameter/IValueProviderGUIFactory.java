/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import org.eclipse.core.runtime.IExecutableExtension;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;

/**
 * Implementations of {@link IValueProviderGUIFactory} are registered as extension to the
 * extension-point <code>org.nightlabs.jfire.reporting.ui.valueProviderGUIFactory</code>
 * and is responsible for creating instances of {@link IValueProviderGUI} for the 
 * {@link ValueProviderID} it is registered for. The factory is registered only by class
 * in the extension and bound to a ValueProvider by querying its id by {@link #getValueProviderID()}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IValueProviderGUIFactory extends IExecutableExtension {

	/**
	 * @return The id of the ValueProvider this factory creates UI for.
	 */
	public ValueProviderID getValueProviderID();

	/**
	 * Create a new instance of {@link IValueProviderGUI} that will create UI that
	 * is used to let the user build report parameters. 
	 * 
	 * @param valueProviderConfig The {@link ValueProviderConfig} the ui can use to build messages etc.
	 * @param isScheduledReport Whether this {@link IValueProviderGUI} is created to configure the parameters of a scheduledReport.
	 * @return A new instance of {@link IValueProviderGUI}.
	 */
	IValueProviderGUI<?> createValueProviderGUI(ValueProviderConfig valueProviderConfig, boolean isScheduledReport);
	
}
