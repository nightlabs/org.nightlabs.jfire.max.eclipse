package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthorityPage;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthoritySection;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;

public class AuthorityPage
extends AbstractAuthorityPage
{
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new AuthorityPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new AuthorityPageController(editor);
		}
	}

	public AuthorityPage(FormEditor editor) {
		super(editor, AuthorityPage.class.getName());
	}

	@Override
	protected AbstractAuthoritySection createAuthoritySection(Composite parent) {
		return new AuthoritySection(this, parent);
	}

	@Override
	protected AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return ((AuthorityPageController)getPageController()).getAuthorityPageControllerHelper();
	}
}
