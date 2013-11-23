/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import org.eclipse.core.runtime.ListenerList;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractValueProviderGUI<OutputType> implements IValueProviderGUI<OutputType> {

	private ListenerList listeners = new ListenerList();
	private ValueProviderConfig valueProviderConfig;
	
	/**
	 * 
	 */
	public AbstractValueProviderGUI(ValueProviderConfig valueProviderConfig) {
		this.valueProviderConfig = valueProviderConfig;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#addValueProviderGUIListener(org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIListener)
	 */
	public void addValueProviderGUIListener(IValueProviderGUIListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#removeValueProviderGUIListener(org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIListener)
	 */
	public void removeValueProviderGUIListener(IValueProviderGUIListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyOutputChanged() {
		Object[] ls = listeners.getListeners();
		for (int i = 0; i < ls.length; i++) {
			IValueProviderGUIListener listener = (IValueProviderGUIListener) ls[i];
			listener.providerOutputValueChanged();
		}
	}

	public ValueProviderConfig getValueProviderConfig() {
		return valueProviderConfig;
	}

	public void setValueProviderConfig(ValueProviderConfig valueProviderConfig) {
		this.valueProviderConfig = valueProviderConfig;
	}
	
	public void setInitialValue(OutputType initalValue) {
	};
}
