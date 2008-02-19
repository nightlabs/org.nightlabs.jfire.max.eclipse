package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeDetailPage
extends EntityEditorPageWithProgress
implements IProductTypeDetailPage
{
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public AbstractProductTypeDetailPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	private int sectionStyle = ExpandableComposite.TITLE_BAR;
	/**
	 * returns the sectionStyle.
	 * @return the sectionStyle
	 */
	public int getSectionStyle() {
		return sectionStyle;
	}
	/**
	 * sets the sectionStyle
	 * @param sectionStyle the sectionStyle to set
	 */
	public void setSectionStyle(int sectionStyle) {
		this.sectionStyle = sectionStyle;
	}





//	private AbstractProductTypeNameSection nameSection = null;
//	public AbstractProductTypeNameSection getNameSection() {
//	return nameSection;
//	}
//	protected abstract AbstractProductTypeNameSection createNameSection();
	private IProductTypeSectionPart nameSection = null;
	public IProductTypeSectionPart getNameSection() {
		return nameSection;
	}
	protected abstract IProductTypeSectionPart createNameSection(Composite parent);




	private IProductTypeSectionPart vendorSection = null;
	public IProductTypeSectionPart getVendorSection() {
		return vendorSection;
	}
	protected abstract IProductTypeSectionPart createVendorSection(Composite parent);





	private IProductTypeSectionPart ownerSection = null;
	public IProductTypeSectionPart getOwnerSection() {
		return ownerSection;
	}
	protected abstract IProductTypeSectionPart createOwnerSection(Composite parent);






//	private AbstractNestedProductTypeSection nestedProductTypeSection = null;
//	public AbstractNestedProductTypeSection getNestedProductTypeSection() {
//	return nestedProductTypeSection;
//	}
//	protected abstract AbstractNestedProductTypeSection createNestedProductTypesSection();
	private IProductTypeSectionPart nestedProductTypeSection = null;
	public IProductTypeSectionPart getNestedProductTypeSection() {
		return nestedProductTypeSection;
	}
	protected abstract IProductTypeSectionPart createNestedProductTypesSection(Composite parent);

//	private AbstractSaleAccessControlSection saleAccessControlSection = null;
//	public AbstractSaleAccessControlSection getSaleAccessControlSection() {
//	return saleAccessControlSection;
//	}
//	protected abstract AbstractSaleAccessControlSection createSaleAccessControlSection();
	private IProductTypeSectionPart saleAccessControlSection = null;
	public IProductTypeSectionPart getSaleAccessControlSection() {
		return saleAccessControlSection;
	}
	protected abstract IProductTypeSectionPart createSaleAccessControlSection(Composite parent);


	@Override
	protected void addSections(Composite parent)
	{
		nameSection = createNameSection(parent);
		if (nameSection != null) {
			nameSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			getManagedForm().addPart(nameSection);
		}

		nestedProductTypeSection = createNestedProductTypesSection(parent);
		if (nestedProductTypeSection != null) {
			nestedProductTypeSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
			getManagedForm().addPart(nestedProductTypeSection);
		}





		ownerSection = createOwnerSection(parent);
		if (ownerSection != null) {
			//ownerSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));		
			ownerSection.getSection().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));	
			getManagedForm().addPart(ownerSection);
		}

		vendorSection = createVendorSection(parent);
		if (vendorSection != null) {
			//vendorSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
			vendorSection.getSection().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));	
			getManagedForm().addPart(vendorSection);
		}		



		saleAccessControlSection = createSaleAccessControlSection(parent);
		if (saleAccessControlSection != null) {
			saleAccessControlSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			getManagedForm().addPart(saleAccessControlSection);
		}


	}



	@Override
	protected void asyncCallback()
	{
		final AbstractProductTypePageController controller = (AbstractProductTypePageController) getPageController();
		final ProductType productType = controller.getProductType();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setProductType(productType);
				switchToContent();
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPage.pageFormTitle"); //$NON-NLS-1$
	}

	protected void setProductType(ProductType productType)
	{
		if (productType == null) {
			getManagedForm().getForm().getForm().setMessage("No product type selected.", IMessageProvider.INFORMATION);
			RCPUtil.setControlEnabledRecursive(getManagedForm().getForm(), false);
		}
		else if (productType.isClosed()) {
			getManagedForm().getForm().getForm().setMessage(
					Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPage.productTypeClosedMessage"),  //$NON-NLS-1$
					IMessageProvider.INFORMATION);
			RCPUtil.setControlEnabledRecursive(getManagedForm().getForm(), false);
		}
		else {
			getManagedForm().getForm().getForm().setMessage(null, IMessageProvider.INFORMATION);
			RCPUtil.setControlEnabledRecursive(getManagedForm().getForm(), true);
		}

		if (nameSection != null)
			nameSection.setProductType(productType);
		if (nestedProductTypeSection != null)
			nestedProductTypeSection.setProductType(productType);
		if (saleAccessControlSection != null)
			saleAccessControlSection.setProductType(productType);
		if (ownerSection != null)
			ownerSection.setProductType(productType);						
		if (vendorSection != null)
			vendorSection.setProductType(productType);						



	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeDetailPage#getProductTypeDetailPageController()
	 */
	public IProductTypeDetailPageController getProductTypeDetailPageController() {
		if (getPageController() instanceof IProductTypeDetailPageController)
			return (IProductTypeDetailPageController) getPageController();
		throw new IllegalStateException("AbstractProductTypeDetailPage should be used with a controller implementing " + IProductTypeDetailPageController.class.getName() + ". The controller is " + getPageController().getClass().getName() + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}