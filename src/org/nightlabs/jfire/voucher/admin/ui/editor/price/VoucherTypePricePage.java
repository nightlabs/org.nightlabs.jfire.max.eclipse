package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeDetailPageController;
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

		super(editor, VoucherTypePricePage.class.getName(),Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherTypePricePage.TitlePage")); //$NON-NLS-1$

		// TODO Auto-generated constructor stub
	}

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new VoucherTypePricePage(formEditor,VoucherTypePricePage.class.getName(),Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherTypePricePage.TitleSection"));  //$NON-NLS-1$
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


	@Override
	protected void addSections(Composite parent) 
	{


		voucherPriceConfigSection = new VoucherPriceConfigSection(this, parent, ExpandableComposite.TITLE_BAR);
//		default is FILL_BOTH
//		voucherPriceConfigSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));		
		getManagedForm().addPart(voucherPriceConfigSection);
	}

	@Override
	protected void asyncCallback() 
	{
		VoucherTypeDetailPageController controller = (VoucherTypeDetailPageController) getPageController();
		final VoucherType voucherType = controller.getProductType();
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {			

				if ( voucherPriceConfigSection != null && !voucherPriceConfigSection.getSection().isDisposed())
					voucherPriceConfigSection.setVoucherType(voucherType);

				switchToContent();				
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherTypePricePage.PageFormTitle"); //$NON-NLS-1$
	}

}
