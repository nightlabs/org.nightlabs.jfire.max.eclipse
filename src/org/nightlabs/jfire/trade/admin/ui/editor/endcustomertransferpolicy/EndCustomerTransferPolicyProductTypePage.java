package org.nightlabs.jfire.trade.admin.ui.editor.endcustomertransferpolicy;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class EndCustomerTransferPolicyProductTypePage
extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new EndCustomerTransferPolicyProductTypePage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new EndCustomerTransferPolicyProductTypePageController(editor);
		}
	}

	public EndCustomerTransferPolicyProductTypePage(FormEditor editor) {
		super(editor, EndCustomerTransferPolicyProductTypePage.class.getName(), "End-customer transfer policy");
	}

	private Display display;
	private EndCustomerTransferPolicySection endCustomerTransferPolicySection;

	@Override
	protected void addSections(Composite parent) {
		endCustomerTransferPolicySection = new EndCustomerTransferPolicySection(this, parent);
		getManagedForm().addPart(endCustomerTransferPolicySection);
		display = endCustomerTransferPolicySection.getSection().getDisplay();
		endCustomerTransferPolicySection.setEndCustomerTransferPolicyControllerHelper(
				getPageController().getEndCustomerTransferPolicyControllerHelper()
		);
	}

	@Override
	protected String getPageFormTitle() {
		return "End-customer transfer policy";
	}

	@Override
	public EndCustomerTransferPolicyProductTypePageController getPageController() {
		return (EndCustomerTransferPolicyProductTypePageController) super.getPageController();
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		super.handleControllerObjectModified(modifyEvent);
		display.asyncExec(new Runnable() {
			public void run() {
				if (endCustomerTransferPolicySection.getSection().isDisposed())
					return;

				endCustomerTransferPolicySection.setEndCustomerTransferPolicyControllerHelper(
						getPageController().getEndCustomerTransferPolicyControllerHelper()
				);
			}
		});

	}
}
