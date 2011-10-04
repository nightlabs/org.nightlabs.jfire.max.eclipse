/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.action.schedule;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduleReportAction extends ReportRegistryItemAction {

	/**
	 * 
	 */
	public ScheduleReportAction() {
	}

	/**
	 * @param text
	 */
	public ScheduleReportAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public ScheduleReportAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public ScheduleReportAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction#run(java.util.Collection)
	 */
	@Override
	public void run(Collection<ReportRegistryItem> reportRegistryItems) {
		if (reportRegistryItems != null
				&& !reportRegistryItems.isEmpty()){
			CreateScheduledReportWizard.open(RCPUtil.getActiveShell(), (ReportLayout) reportRegistryItems.iterator().next());
		}
	}

	@Override
	public boolean calculateEnabled(Collection<ReportRegistryItem> registryItems) {
		if (registryItems.size() != 1)
			return false;
		return registryItems.iterator().next() instanceof ReportLayout;
	}
}
