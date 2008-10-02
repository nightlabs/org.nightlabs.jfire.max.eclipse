package org.nightlabs.jfire.trade.ui.repository.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.trade.ui.resource.Messages;

class ProductTransferPage
		extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new ProductTransferPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ProductTransferPageController(editor);
		}
	}

	private ProductTransferFilterSection productTransferFilterSection;
	private ProductTransferListSection productTransferListSection;

	public ProductTransferPage(FormEditor editor)
	{
		super(editor, ProductTransferPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.ProductTransferPage.title")); // this is shown in the tab on the bottom //$NON-NLS-1$
	}

	@Override
	protected void addSections(Composite parent)
	{
		ProductTransferPageController controller = (ProductTransferPageController) getPageController();

		productTransferFilterSection = new ProductTransferFilterSection(this, parent, controller);
		getManagedForm().addPart(productTransferFilterSection);

		productTransferListSection = new ProductTransferListSection(this, parent, controller);
		getManagedForm().addPart(productTransferListSection);
	}

	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent)
	{
		switchToContent(); // multiple calls don't hurt

		
	}

	@Override
	protected String getPageFormTitle() // this is shown on top as title
	{
		return Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.ProductTransferPage.title"); //$NON-NLS-1$
	}

}
