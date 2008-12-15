/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.IReportRegistryItemEditorInput;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * {@link IEntityEditorPageController} that loads a {@link ReportRegistryItem}.
 * Fetch-groups defined in constructor.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportRegistryItemPageController extends
		ActiveEntityEditorPageController<ReportRegistryItem> {

	private String[] fetchGroups;
	private ReportRegistryItemID reportRegistryItemID;

	/**
	 * @param editor
	 */
	public ReportRegistryItemPageController(EntityEditor editor, String[] fetchGroups) {
		super(editor);
		this.fetchGroups = fetchGroups;
		reportRegistryItemID = ((IReportRegistryItemEditorInput) editor.getEditorInput()).getReportRegistryItemID();
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ReportRegistryItemPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#getEntityFetchGroups()
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return fetchGroups;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#retrieveEntity(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ReportRegistryItem retrieveEntity(ProgressMonitor monitor) {
		return ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(reportRegistryItemID, fetchGroups, monitor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#storeEntity(java.lang.Object, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ReportRegistryItem storeEntity(ReportRegistryItem reportRegistryItem, ProgressMonitor monitor) {
		try {
			return ReportRegistryItemDAO.sharedInstance().storeReportRegistryItem(
					reportRegistryItem, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
