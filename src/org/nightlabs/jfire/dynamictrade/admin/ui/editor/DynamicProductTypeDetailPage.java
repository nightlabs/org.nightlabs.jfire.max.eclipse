package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPage;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorConfigSection;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeDetailPage
extends AbstractProductTypeDetailPage
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new DynamicProductTypeDetailPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new DynamicProductTypeDetailPageController(editor);
		}
	}

	public DynamicProductTypeDetailPage(FormEditor editor) {
		super(editor, DynamicProductTypeDetailPage.class.getName(), Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypeDetailPage.title")); //$NON-NLS-1$
	}

	@Override
	protected IProductTypeSectionPart createNameSection(Composite parent)
	{
		return new DynamicProductTypeNameSection(this, parent, getSectionStyle());
	}

	@Override
	protected IProductTypeSectionPart createNestedProductTypesSection(Composite parent)
	{
		return null;
	}

	@Override
	protected IProductTypeSectionPart createOwnerVendorSection(Composite parent)
	{
		return new OwnerVendorConfigSection(this, parent, getSectionStyle());

	}
	
	
	@Override
	protected IProductTypeSectionPart createSaleAccessControlSection(Composite parent)
	{
		return new DynamicProductTypeSaleAccessControlSection(this, parent, getSectionStyle());
	}

//	private DynamicProductTypeNameSection productTypeNameSection = null;
//	public DynamicProductTypeNameSection getProductTypeNameSection() {
//		return productTypeNameSection;
//	}
//
//	private DynamicProductTypeSaleAccessControlSection saleAccessSection = null;
//	public DynamicProductTypeSaleAccessControlSection getSaleAccessSection() {
//		return saleAccessSection;
//	}
//
//	private int sectionStyle = ExpandableComposite.TITLE_BAR;
//
//	@Override
//	protected void addSections(Composite parent)
//	{
//		productTypeNameSection = new DynamicProductTypeNameSection(this, parent, sectionStyle,
//				Messages.getString("DynamicProductTypeDetailPage.name")); //$NON-NLS-1$
//		productTypeNameSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		getManagedForm().addPart(productTypeNameSection);
//
//		saleAccessSection = new DynamicProductTypeSaleAccessControlSection(this, parent, sectionStyle,
//				Messages.getString("DynamicProductTypeDetailPage.saleAccessControl")); //$NON-NLS-1$
//		saleAccessSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		getManagedForm().addPart(saleAccessSection);
//	}
//
//	@Override
//	protected void asyncCallback()
//	{
//		final DynamicProductTypeDetailPageController controller = (DynamicProductTypeDetailPageController) getPageController();
//		final ProductType productType = controller.getProductType();
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				productTypeNameSection.setProductType(productType);
//				saleAccessSection.setProductType(productType);
//				if (productType.isClosed()) {
//					getManagedForm().getForm().getForm().setMessage(
//							Messages.getString("DynamicProductTypeDetailPage.productTypeStatusClosedMessage"),  //$NON-NLS-1$
//							IMessageProvider.INFORMATION);
//					RCPUtil.setControlEnabledRecursive(getManagedForm().getForm(), false);
//				}
//				switchToContent();
//			}
//		});
//	}
//
//	@Override
//	protected String getPageFormTitle() {
//		return Messages.getString("DynamicProductTypeDetailPage.productDetail"); //$NON-NLS-1$
//	}

}
