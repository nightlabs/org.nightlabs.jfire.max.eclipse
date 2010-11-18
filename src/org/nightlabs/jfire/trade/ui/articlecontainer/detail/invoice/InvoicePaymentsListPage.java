package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 * the Editor page which lists all the payments of an Invoice.
 * 
 * @author Fitas Amine - fitas at NightLabs dot de
 */
public class InvoicePaymentsListPage extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new InvoicePaymentsListPage(formEditor);
		}
		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new InvoicePaymentsListPageController(editor);
		}
	}

	public static final String PAGE_ID = InvoicePaymentsListPage.class.getName();

	InvoicePaymentsListSection invoicePaymentSection;

	/**
	 * @param editor
	 */
	public InvoicePaymentsListPage(FormEditor editor) {
		super(editor, PAGE_ID, "Payments"); 
	}

	@Override
	protected void addSections(Composite parent) {
		
		final InvoicePaymentsListPageController controller = (InvoicePaymentsListPageController) getPageController();

		invoicePaymentSection = new InvoicePaymentsListSection(this, parent, controller);
		invoicePaymentSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(invoicePaymentSection);
		
		if (controller.isLoaded()) {
			invoicePaymentSection.setPayableObjectID(controller.getArticleContainerID());
		}		
	}

	@Override
	protected String getPageFormTitle() {
		return "Payments";
	}
	
	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {	
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				InvoicePaymentsListPageController controller = (InvoicePaymentsListPageController)getPageController();				
				if(invoicePaymentSection != null)
					invoicePaymentSection.setPayableObjectID(controller.getArticleContainerID());						}
		});
		switchToContent();	
	}
}
