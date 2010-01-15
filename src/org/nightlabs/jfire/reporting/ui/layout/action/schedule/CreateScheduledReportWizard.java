/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.action.schedule;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.dao.ScheduledReportDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CreateScheduledReportWizard extends DynamicPathWizard {

	private CreateScheduledReportWizardPage timePatternWizardPage;
	private ReportLayout reportLayout;
	
	/**
	 * 
	 */
	public CreateScheduledReportWizard(ReportLayout reportLayout) {
		this.reportLayout = reportLayout;
	}

	@Override
	public void addPages() {
		timePatternWizardPage = new CreateScheduledReportWizardPage(this);
		addPage(timePatternWizardPage);
	}
	
	ReportLayout getReportLayout() {
		return reportLayout;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ScheduledReport report = null;
		try {
			report = new ScheduledReport(
					Login.getLogin().getUser(new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()),
					IDGenerator.nextID(ScheduledReport.class)
					);
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		
		ScheduledReportDAO.sharedInstance().storeJDOObject(report, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());
		
		return true;
	}
	
	public static void open(Shell shell, ReportLayout reportLayout) {
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(shell, new CreateScheduledReportWizard(reportLayout));
		dlg.open();
	}

}
