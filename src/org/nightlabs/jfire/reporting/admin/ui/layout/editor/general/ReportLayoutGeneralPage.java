/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.editor.general;

import javax.jdo.FetchPlan;

import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.jfire.reporting.admin.ui.category.editor.ReportDetailPage;
import org.nightlabs.jfire.reporting.admin.ui.category.editor.ReportRegistryItemPageController;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.ReportLayoutEntityEditor;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportLayoutGeneralPage extends ReportLayoutEntityEditor {

	/**
	 *
	 */
	public ReportLayoutGeneralPage() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.layout.editor.ReportLayoutEntityEditor#createFormPage(org.nightlabs.base.ui.entity.editor.EntityEditor)
	 */
	@Override
	protected IFormPage createFormPage(EntityEditor entityEditor) {
		return new ReportDetailPage(entityEditor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.layout.editor.ReportLayoutEntityEditor#createPageController(org.nightlabs.base.ui.entity.editor.EntityEditor)
	 */
	@Override
	protected IEntityEditorPageController createPageController(EntityEditor entityEditor) {
		return new ReportRegistryItemPageController(entityEditor, new String[] {
				FetchPlan.DEFAULT,
				ReportRegistryItem.FETCH_GROUP_NAME, ReportRegistryItem.FETCH_GROUP_DESCRIPTION
		});
	}

	@Override
	public String getId() {
		return ReportDetailPage.PAGE_ID;
	}

}
