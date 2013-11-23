/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.action;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReportManagerRemote;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author sk
 *
 */
public class AddScheduledReportAction extends AbstractScheduledReportAction {

	/**
	 * 
	 */
	public AddScheduledReportAction() {
	}
	
	@Override
	public void run() {
		Login login;
		try {
			login = Login.getLogin();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		User user = login.getUser(new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		ScheduledReport scheduledReport = new ScheduledReport(user, IDGenerator.nextID(ScheduledReport.class));
		ScheduledReportManagerRemote scheduledReportManager = JFireEjb3Factory.getRemoteBean(ScheduledReportManagerRemote.class, login.getInitialContextProperties());
		scheduledReportManager.storeScheduledReport(scheduledReport, false, null, -1);
	}

}
