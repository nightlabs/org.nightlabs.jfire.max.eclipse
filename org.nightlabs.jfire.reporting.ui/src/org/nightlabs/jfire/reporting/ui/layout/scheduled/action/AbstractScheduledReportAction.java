package org.nightlabs.jfire.reporting.ui.layout.scheduled.action;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractScheduledReportAction extends Action implements IScheduledReportAction {

	private Collection<ScheduledReport> scheduledReports;
	
	/**
	 * 
	 */
	public AbstractScheduledReportAction() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.action.IScheduledReportAction#calculateEnabled(java.util.Collection)
	 */
	@Override
	public boolean calculateEnabled(Collection<ScheduledReport> scheduledReports) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.action.IScheduledReportAction#calculateVisible(java.util.Collection)
	 */
	@Override
	public boolean calculateVisible(Collection<ScheduledReport> scheduledReports) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.action.IScheduledReportAction#getScheduledReports()
	 */
	@Override
	public Collection<ScheduledReport> getScheduledReports() {
		return scheduledReports;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.action.IScheduledReportAction#setScheduledReports(java.util.Collection)
	 */
	@Override
	public void setScheduledReports(Collection<ScheduledReport> scheduledReports) {
		this.scheduledReports = scheduledReports;
	}

}
