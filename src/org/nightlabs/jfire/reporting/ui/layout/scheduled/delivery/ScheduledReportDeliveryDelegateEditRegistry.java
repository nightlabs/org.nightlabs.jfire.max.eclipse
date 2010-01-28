package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportDeliveryDelegateEditRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = ReportingPlugin.class.getPackage().getName() + ".scheduledReportDeliveryDelegateEdit";

	/**
	 * Map to hold all contributions. The key is the id of the factory (usually the class name of
	 * the implementaion of {@link IScheduledReportDeliveryDelegate}, value is the contributed
	 * factory
	 */
	private Map<String, IScheduledReportDeliveryDelegateEditFactory> factories = new HashMap<String, IScheduledReportDeliveryDelegateEditFactory>();

	/**
	 * Creates a new {@link ScheduledReportDeliveryDelegateEditRegistry}. Usually the
	 * {@link #sharedInstance()} should be used.
	 */
	protected ScheduledReportDeliveryDelegateEditRegistry() {
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
		if ("scheduledReportDeliveryDelegateEditFactory".equalsIgnoreCase(element.getName())) {
			String id = element.getAttribute("id");
			if (id == null || id.isEmpty()) {
				throw new EPProcessorException("Attribute id must be defined", extension);
			}
			IScheduledReportDeliveryDelegateEditFactory factory = (IScheduledReportDeliveryDelegateEditFactory) element
					.createExecutableExtension("class");
			factories.put(id, factory);
		}
	}
	
	public Collection<IScheduledReportDeliveryDelegateEditFactory> getFactories() {
		checkProcessing();
		return Collections.unmodifiableCollection(factories.values());
	}
	
	public IScheduledReportDeliveryDelegateEditFactory getFactory(String id) {
		checkProcessing();
		return factories.get(id);
	}
	
	/** Static shared instance of ScheduledReportDeliveryDelegateEditRegistry */
	private static ScheduledReportDeliveryDelegateEditRegistry sharedInstance;

	/**
	 * @return The (lazily created) singleton of {@link ScheduledReportDeliveryDelegateEditRegistry}.
	 */
	public static ScheduledReportDeliveryDelegateEditRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ScheduledReportDeliveryDelegateEditRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new ScheduledReportDeliveryDelegateEditRegistry();
				}
			}
		}
		return sharedInstance;
	}

}
