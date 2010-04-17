/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.action;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.action.registry.ActionVisibilityDecider;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportActionRegistry extends AbstractActionRegistry {

	public static final String EXTENSION_POINT_ID = ReportingPlugin.class.getPackage().getName() + ".scheduledReportAction";
	
	private static final String ACTION_ELEMENT_NAME = "scheduledReportAction";
	
	public ScheduledReportActionRegistry(ActionVisibilityDecider actionVisibilityDecider) {
		super(actionVisibilityDecider);
	}

	/**
	 * 
	 */
	public ScheduledReportActionRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.registry.AbstractActionRegistry#createActionOrContributionItem(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) throws EPProcessorException {
		try {
			return (IScheduledReportAction) element.createExecutableExtension("class");
		} catch (Exception e) {
			throw new EPProcessorException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	@Override
	protected String getActionElementName() {
		return ACTION_ELEMENT_NAME;
	}

	
	private static ScheduledReportActionRegistry sharedInstance;
	private static boolean initializingSharedInstance = false;
	public static synchronized ScheduledReportActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				sharedInstance = new ScheduledReportActionRegistry();
				sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return sharedInstance;
	}
	
}
