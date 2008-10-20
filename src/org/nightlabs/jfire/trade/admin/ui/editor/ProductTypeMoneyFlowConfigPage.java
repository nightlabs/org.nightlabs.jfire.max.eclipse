package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeMoneyFlowConfigPage
extends EntityEditorPageWithProgress
{
//	/**
//	 * The Factory is registered to the extension-point and creates
//	 * new instances of {@link TicketingMoneyFlowConfigPage}.
//	 */
//	public static class Factory implements IEntityEditorPageFactory {
//		public IFormPage createPage(FormEditor formEditor) {
//			return new ProductTypeMoneyFlowConfigPage(formEditor);
//		}
//
//		public IEntityEditorPageController createPageController(EntityEditor editor) {
//			return new ProductTypeMoneyFlowConfigPageController(editor);
//		}
//	}
	
	public ProductTypeMoneyFlowConfigPage(FormEditor editor) {
		super(editor, ProductTypeMoneyFlowConfigPage.class.getName(),
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigPage.title")); //$NON-NLS-1$
	}

	private ProductTypeMoneyFlowConfigSection moneyFlowSection = null;
	public ProductTypeMoneyFlowConfigSection getMoneyFlowSection() {
		return moneyFlowSection;
	}
	
	@Override
	protected void addSections(Composite parent) {
		moneyFlowSection = new ProductTypeMoneyFlowConfigSection(this, parent,
				ExpandableComposite.TITLE_BAR,
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigPage.moneyFlowSection.title")); //$NON-NLS-1$
		getManagedForm().addPart(moneyFlowSection);
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		final ProductTypeMoneyFlowConfigPageController controller = (ProductTypeMoneyFlowConfigPageController) getPageController();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
//				moneyFlowSection.setProductTypeID(controller.getProductTypeID());
				if (isDisposed())
					return; // Do nothing if UI is already disposed
				moneyFlowSection.setProductType(controller.getProductType());
				if (controller.getProductType() != null && controller.getProductType().isClosed()) {
					moneyFlowSection.setMessage(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigPage.moneyFlowSectionMessage_productTypeClosed")); //$NON-NLS-1$
					RCPUtil.setControlEnabledRecursive(moneyFlowSection.getMoneyFlowConfigComposite(), false);
				}
				switchToContent();
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigPage.pageFormTitle"); //$NON-NLS-1$
	}
	
	

}
