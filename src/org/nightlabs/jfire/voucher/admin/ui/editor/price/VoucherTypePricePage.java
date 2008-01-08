package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherLayoutSection;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeDetailPage;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeDetailPageController;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeNameSection;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeSaleAccessControlSection;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author fitas [at] NightLabs [dot] de
 *
 */
public class VoucherTypePricePage 
extends EntityEditorPageWithProgress 

{
	public VoucherTypePricePage(FormEditor editor, String id, String name) {
		//super(editor, id, name);
		
		super(editor, VoucherTypePricePage.class.getName(), 
				Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherTypePricePage.title")); //$NON-NLS-1$

		// TODO Auto-generated constructor stub
	}

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new VoucherTypePricePage(formEditor,VoucherTypePricePage.class.getName(),Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherTypePricePage.title")); //$NON-NLS-1$
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			
			VoucherTypeDetailPageController controller = editor.getController().getSinglePageController(VoucherTypeDetailPageController.class);
			
			
			if(controller == null)
		    controller = new VoucherTypeDetailPageController(editor);
			
			return controller;
		}
	}
	

	
	private VoucherPriceConfigSection voucherPriceConfigSection = null;
	
	public VoucherPriceConfigSection getVoucherLayoutSection() {
		return voucherPriceConfigSection;
	}
	
//	private VoucherTypeNameSection voucherTypeNameSection = null;
//	public VoucherTypeNameSection getVoucherTypeNameSection() {
//		return voucherTypeNameSection;
//	}
//	
//	private VoucherTypeSaleAccessControlSection voucherTypeSaleAccessControlSection = null;
//	public VoucherTypeSaleAccessControlSection getVoucherSaleAccessControlSection() {
//		return voucherTypeSaleAccessControlSection;
//	}
	
	@Override
	protected void addSections(Composite parent) 
	{
		//super.addSections(parent);
		
		
		voucherPriceConfigSection = new VoucherPriceConfigSection(this, parent, ExpandableComposite.TITLE_BAR);
		voucherPriceConfigSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		getManagedForm().addPart(voucherPriceConfigSection);
	}

	@Override
	protected void asyncCallback() 
	{
		VoucherTypeDetailPageController controller = (VoucherTypeDetailPageController) getPageController();
		final VoucherType voucherType = (VoucherType) controller.getProductType();
		Display.getDefault().asyncExec(new Runnable() {
			
			public void run() {
				
				voucherPriceConfigSection.setVoucherType(voucherType);
				//getNameSection().setProductType(voucherType);
				//getSaleAccessControlSection().setProductType(voucherType);
				
				if (voucherType.isClosed()) {
					getManagedForm().getForm().getForm().setMessage(
							Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeDetailPage.productTypeClosedMessage"), //$NON-NLS-1$
							IMessageProvider.INFORMATION);
					RCPUtil.setControlEnabledRecursive(getManagedForm().getForm(), false);
			
				}
				switchToContent();				
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherTypePricePage.pageFormTitle"); //$NON-NLS-1$
	}
	
}
