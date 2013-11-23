package org.nightlabs.jfire.reporting.ui.layout.scheduled.action;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.dao.ScheduledReportDAO;
import org.nightlabs.jfire.reporting.scheduled.id.ScheduledReportID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DeleteScheduledReportAction extends AbstractScheduledReportAction {

	/**
	 * 
	 */
	public DeleteScheduledReportAction() {
	}
	
	@Override
	public void run() {
		final Collection<ScheduledReport> scheduledReports = getScheduledReports();
		if (scheduledReports != null) {
			Job deleteJob = new Job("Deleting Scheduled reports") {
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					monitor.beginTask("Deleting Scheduled reports", scheduledReports.size());
					for (ScheduledReport scheduledReport : scheduledReports) {
						ScheduledReportDAO.sharedInstance().deleteScheduledReport(
								(ScheduledReportID) JDOHelper.getObjectId(scheduledReport), new SubProgressMonitor(monitor, 1));
					}
					monitor.done();
					return Status.OK_STATUS;
				}
				
			};
			deleteJob.schedule();
		}
		super.run();
	}

}
