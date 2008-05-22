package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageControllerModifyListener;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.admin.ui.editor.user.RoleGroupSecurityPreferencesModel;
import org.nightlabs.jfire.base.admin.ui.editor.user.RoleGroupsSection;
import org.nightlabs.jfire.security.User;

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
	private UserSection	userSection;
	private RoleGroupsSection roleGroupsSection;

	@Override
	protected void addSections(Composite parent) {
		authoritySection = new AuthoritySection(this, parent);
		getManagedForm().addPart(authoritySection);

		userSection = new UserSection(this, parent);
		getManagedForm().addPart(userSection);

		roleGroupsSection = new RoleGroupsSection(this, parent, true);
		getManagedForm().addPart(roleGroupsSection);

		userSection.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				RoleGroupSecurityPreferencesModel model = null;
				List<User> selectedUsers = userSection.getSelectedUsers();
				User selectedUser = null;
				if (!selectedUsers.isEmpty())
					selectedUser = selectedUsers.get(0);

				if (selectedUser != null) {
					AuthorityPageControllerHelper helper = ((AuthorityPageController)getPageController()).getAuthorityPageControllerHelper();
					model = helper.getUser2RoleGroupSecurityPreferencesModel().get(selectedUser);
				}

				roleGroupsSection.setModel(model);
			}
		});

		// TODO Is this the way the API is supposed to be used? Have to check tomorrow. Marco.
		getPageController().addModifyListener(new IEntityEditorPageControllerModifyListener() {
			@Override
			public void controllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
				authoritySection.setProductTypePageController((AuthorityPageController)getPageController());
				userSection.setProductTypePageController((AuthorityPageController)getPageController());
			}
		});
	}

	@Override
	protected void asyncCallback() {
		switchToContent(); // necessary? TODO talk to Bieber - he already removed this method in a branch - why is it still here? Marco.
	}

	@Override
	protected String getPageFormTitle() {
		return "Authority configuration";
	}

}
