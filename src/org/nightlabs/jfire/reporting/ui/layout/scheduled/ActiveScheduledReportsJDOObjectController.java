/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.dao.ScheduledReportDAO;
import org.nightlabs.jfire.reporting.scheduled.id.ScheduledReportID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * Active object controller for {@link ScheduledReport}s of the current user.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ActiveScheduledReportsJDOObjectController extends ActiveJDOObjectController<ScheduledReportID, ScheduledReport> {

	public ActiveScheduledReportsJDOObjectController() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController#getJDOObjectClass()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Class getJDOObjectClass() {
		return ScheduledReport.class;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController#retrieveJDOObjects(java.util.Set, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected Collection<ScheduledReport> retrieveJDOObjects(Set<ScheduledReportID> objectIDs, ProgressMonitor monitor) {
		return ScheduledReportDAO.sharedInstance().getScheduledReports(objectIDs,
				new String[] { FetchPlan.DEFAULT, ScheduledReport.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, 10));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController#retrieveJDOObjects(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected Collection<ScheduledReport> retrieveJDOObjects(ProgressMonitor monitor) {
		return ScheduledReportDAO.sharedInstance().getScheduledReports(
				new String[] { FetchPlan.DEFAULT, ScheduledReport.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, 10));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController#sortJDOObjects(java.util.List)
	 */
	@Override
	protected void sortJDOObjects(List<ScheduledReport> objects) {
		Collections.sort(objects, new Comparator<ScheduledReport>() {
			@Override
			public int compare(ScheduledReport o1, ScheduledReport o2) {
				return o1.getName().getText().compareTo(o2.getName().getText());
			}
		});
	}

}
