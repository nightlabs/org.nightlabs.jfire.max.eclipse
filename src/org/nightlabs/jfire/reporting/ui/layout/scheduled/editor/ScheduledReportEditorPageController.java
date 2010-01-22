/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.timepattern.TimePatternSetJDOImpl;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.dao.ScheduledReportDAO;
import org.nightlabs.jfire.timer.Task;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportEditorPageController extends ActiveEntityEditorPageController<ScheduledReport> {

	/**
	 * @param editor
	 */
	public ScheduledReportEditorPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ScheduledReportEditorPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#getEntityFetchGroups()
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {
			FetchPlan.DEFAULT,
			ScheduledReport.FETCH_GROUP_NAME,
			ScheduledReport.FETCH_GROUP_RENDER_REPORT_REQUEST,
			ScheduledReport.FETCH_GROUP_TASK,
			ScheduledReport.FETCH_GROUP_REPORTLAYOUT_ID,
			Task.FETCH_GROUP_TIME_PATTERN_SET,
			TimePatternSetJDOImpl.FETCH_GROUP_TIME_PATTERNS
		};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#retrieveEntity(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ScheduledReport retrieveEntity(ProgressMonitor monitor) {
		return ScheduledReportDAO.sharedInstance().getScheduledReport(
				((ScheduledReportEditorInput) getEntityEditor().getEditorInput()).getJDOObjectID(), 
				getEntityFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#storeEntity(java.lang.Object, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ScheduledReport storeEntity(ScheduledReport controllerObject, ProgressMonitor monitor) {
		return ScheduledReportDAO.sharedInstance().storeJDOObject(controllerObject, true, getEntityFetchGroups(),
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

}
