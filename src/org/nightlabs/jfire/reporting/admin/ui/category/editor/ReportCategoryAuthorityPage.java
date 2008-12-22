/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthorityPage;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;

/**
 * Authority page for the {@link ReportCategoryEditor}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportCategoryAuthorityPage extends AbstractAuthorityPage {

	public static final String PAGE_ID = ReportCategoryAuthorityPage.class.getName();

	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ReportCategoryAuthorityPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new AuthorityPageController(editor);
		}
	}

	/**
	 * @param editor
	 * @param id
	 */
	public ReportCategoryAuthorityPage(FormEditor editor) {
		super(editor, PAGE_ID);
	}

	@Override
	public AuthorityPageController getPageController() {
		return (AuthorityPageController) super.getPageController();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthorityPage#getAuthorityPageControllerHelper()
	 */
	@Override
	protected AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return getPageController().getAuthorityPageControllerHelper();
	}

}
