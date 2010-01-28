package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractDeliveryDelegateEditFactory implements IScheduledReportDeliveryDelegateEditFactory {

	private String id;
	private String name;
	
	/**
	 * 
	 */
	public AbstractDeliveryDelegateEditFactory() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEditFactory#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEditFactory#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setInitializationData(IConfigurationElement element, String attrName, Object configObject) throws CoreException {
		id = checkString(element, "id");
		name = checkString(element, "name");
	}
	
	private String checkString(IConfigurationElement element, String attributeName) throws CoreException {
		String str = element.getAttribute(attributeName);
		if (str == null || str.isEmpty()) {
			throw new CoreException(errorStatus("Attribute " + attributeName + " for IScheduledReportDeliveryDelegateEditFactory "
					+ this.getClass().getName() + " is invalid"));
		}
		return str;
	}
	
	private IStatus errorStatus(String msg) {
		return new Status(IStatus.ERROR, ReportingPlugin.PLUGIN_ID, msg);
	}

}
