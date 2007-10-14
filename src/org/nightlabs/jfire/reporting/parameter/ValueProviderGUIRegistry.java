/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIRegistry extends AbstractEPProcessor {

	private static final Logger logger = Logger.getLogger(ValueProviderGUIRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.reporting.valueProviderGUIFactory";	 //$NON-NLS-1$
	
	private Map<ValueProviderID, IValueProviderGUIFactory> factories = new HashMap<ValueProviderID, IValueProviderGUIFactory>();
	
	/**
	 * 
	 */
	public ValueProviderGUIRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equalsIgnoreCase("valueProviderGUIFactory")) { //$NON-NLS-1$
			IValueProviderGUIFactory factory = null;
			try {
				factory = (IValueProviderGUIFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}
			if (factories.get(factory.getValueProviderID()) != null)
				logger.warn("Found duplicate registration for valueProviderID "+factory.getValueProviderID()+". Duplicate was from: "+element.getNamespaceIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
			factories.put(factory.getValueProviderID(), factory);
		}
	}

	public IValueProviderGUIFactory getValueProviderGUIFactory(ValueProviderID valueProviderID) {
		checkProcessing();
		return factories.get(valueProviderID);
	}
	
	private static ValueProviderGUIRegistry sharedInstance;
	
	public static ValueProviderGUIRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ValueProviderGUIRegistry();		
		return sharedInstance;
	}

}
