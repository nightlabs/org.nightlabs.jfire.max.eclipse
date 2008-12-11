/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.editor.authority;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class AuthorityPageController extends ActiveEntityEditorPageController<ReportRegistryItem> {

	private AuthorityPageControllerHelper pageControllerHelper = new AuthorityPageControllerHelper();

	/**
	 * @param editor
	 */
	public AuthorityPageController(EntityEditor editor) {
		super(editor);
	}
	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public AuthorityPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	public AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return pageControllerHelper;
	}
	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {FetchPlan.DEFAULT};
	}
	@Override
	protected ReportRegistryItem retrieveEntity(ProgressMonitor monitor) {
		JFireRemoteReportEditorInput input = (JFireRemoteReportEditorInput) getEntityEditor().getEditorInput();
		ReportRegistryItem item = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(input.getReportRegistryItemID(), getEntityFetchGroups(), monitor);
		pageControllerHelper.load(item, monitor);
		return item;
	}
	@Override
	protected ReportRegistryItem storeEntity(
			ReportRegistryItem controllerObject, ProgressMonitor monitor) {
		// Storing data is delegated to the helper, too.
		pageControllerHelper.store(monitor);
		return getControllerObject();	
	}
}
