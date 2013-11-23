/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.action;

import java.util.Collection;

import org.eclipse.jface.action.IAction;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IScheduledReportAction extends IAction {

	/**
	 * Use this method to set the <code>ScheduledReportAction</code>s
	 * this actions is invoked on.
	 * 
	 * @param scheduledReports The {@link ScheduledReport}s to set.
	 */
	public void setScheduledReports(Collection<ScheduledReport> scheduledReports);

	/**
	 * Use this method to get the <code>ScheduledReport</code>s
	 * this actions will be invoked on.
	 * 
	 * @return The current <code>ScheduledReport</code> set
	 */
	public Collection<ScheduledReport> getScheduledReports();
	
	
	public boolean calculateEnabled(Collection<ScheduledReport> scheduledReports);
	
	public boolean calculateVisible(Collection<ScheduledReport> scheduledReports);
}
