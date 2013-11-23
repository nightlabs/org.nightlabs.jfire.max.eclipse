package org.nightlabs.jfire.trade.admin.ui.editor.endcustomerreplicationpolicy;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class EndCustomerReplicationPolicyProductTypePage
extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new EndCustomerReplicationPolicyProductTypePage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new EndCustomerReplicationPolicyProductTypePageController(editor);
		}
	}

	public EndCustomerReplicationPolicyProductTypePage(FormEditor editor) {
		super(editor, EndCustomerReplicationPolicyProductTypePage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.endcustomerreplicationpolicy.EndCustomerReplicationPolicyProductTypePage.page.name")); //$NON-NLS-1$
	}

	private Display display;
	private EndCustomerReplicationPolicySection endCustomerReplicationPolicySection;

	@Override
	protected void addSections(Composite parent) {
		endCustomerReplicationPolicySection = new EndCustomerReplicationPolicySection(this, parent);
		getManagedForm().addPart(endCustomerReplicationPolicySection);
		display = endCustomerReplicationPolicySection.getSection().getDisplay();
		endCustomerReplicationPolicySection.setEndCustomerReplicationPolicyControllerHelper(
				getPageController().getEndCustomerReplicationPolicyControllerHelper()
		);
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.endcustomerreplicationpolicy.EndCustomerReplicationPolicyProductTypePage.page.title"); //$NON-NLS-1$
	}

	@Override
	public EndCustomerReplicationPolicyProductTypePageController getPageController() {
		return (EndCustomerReplicationPolicyProductTypePageController) super.getPageController();
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		super.handleControllerObjectModified(modifyEvent);
		display.asyncExec(new Runnable() {
			public void run() {
				if (endCustomerReplicationPolicySection.getSection().isDisposed())
					return;

				endCustomerReplicationPolicySection.setEndCustomerReplicationPolicyControllerHelper(
						getPageController().getEndCustomerReplicationPolicyControllerHelper()
				);
			}
		});

	}
}
