/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.action.schedule;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.dao.ScheduledReportDAO;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizardHop;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CreateScheduledReportWizard extends DynamicPathWizard {

	private CreateScheduledReportWizardPage createScheduledReportPage;
	private ReportLayout reportLayout;
	private ScheduledReport scheduledReport;
	private ReportParameterWizardHop parameterWizardHop;
	private ScheduledReportDeliveryWizardPage deliveryWizardPage;
	
	/**
	 * 
	 */
	public CreateScheduledReportWizard(ReportLayout reportLayout) {
		this.reportLayout = reportLayout;
	}

	@Override
	public void addPages() {
		try {
			scheduledReport = new ScheduledReport(Login.getLogin().getUser(new String[] { FetchPlan.DEFAULT },
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()), 
					IDGenerator.nextID(ScheduledReport.class));
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		ReportRegistryItemID reportLayoutID = (ReportRegistryItemID) JDOHelper.getObjectId(reportLayout);
		
		scheduledReport.setReportLayout(getReportLayout());
		scheduledReport.getTask().setEnabled(true);
		
		createScheduledReportPage = new CreateScheduledReportWizardPage(this);
		addPage(createScheduledReportPage);
		
		deliveryWizardPage = new ScheduledReportDeliveryWizardPage(this);
		addPage(deliveryWizardPage);
		
		parameterWizardHop = new ReportParameterWizardHop(reportLayoutID, true, null);
		if (parameterWizardHop.hasAcquisitionSetup()) {
			addPage(parameterWizardHop.getEntryPage());
		}
	}
	
	ReportLayout getReportLayout() {
		return reportLayout;
	}
	
	ScheduledReport getScheduledReport() {
		return scheduledReport;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		createScheduledReportPage.commitProperties();
		deliveryWizardPage.commitProperties();
		RenderReportRequest renderReportRequest = scheduledReport.getRenderReportRequest();
		if (renderReportRequest == null) {
			renderReportRequest = new RenderReportRequest();
		}
		renderReportRequest.setParameters(parameterWizardHop.getParameters());
		scheduledReport.setRenderReportRequest(renderReportRequest);
		
		ScheduledReportDAO.sharedInstance().storeJDOObject(scheduledReport, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());
		
		return true;
	}
	
	public static void open(Shell shell, ReportLayout reportLayout) {
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(shell, new CreateScheduledReportWizard(reportLayout));
		dlg.open();
	}

}
