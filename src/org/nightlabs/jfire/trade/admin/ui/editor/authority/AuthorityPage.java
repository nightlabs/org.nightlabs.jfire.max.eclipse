package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.admin.ui.editor.user.RoleGroupsSection;

public class AuthorityPage extends EntityEditorPageWithProgress
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new AuthorityPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new AuthorityPageController(editor);
		}
	}

	public AuthorityPage(FormEditor editor) {
		super(editor, AuthorityPage.class.getName(), "Authority");
	}

	private AuthoritySection authoritySection;
	private RoleGroupsSection roleGroupsSection;

	@Override
	protected void addSections(Composite parent) {
		authoritySection = new AuthoritySection(this, parent);
		getManagedForm().addPart(authoritySection);

		roleGroupsSection = new RoleGroupsSection(this, parent, true);
		getManagedForm().addPart(roleGroupsSection);
	}

	@Override
	protected void asyncCallback() {
		switchToContent(); // necessary?
	}

	@Override
	protected String getPageFormTitle() {
		return "Authority configuration";
	}

}
