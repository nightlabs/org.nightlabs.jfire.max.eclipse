package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.progress.ProgressMonitor;


/**
 * @author Fitas [at] NightLabs [dot] de
 *
 */
public class VendorConfigSection  
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private InheritAction inheritAction;
	private FadeableComposite fadeableComposite;
	private LegalEntityEditComposite vendorEditComposite = null;
	private ProductType productType = null;

	public VendorConfigSection(IFormPage page,
			Composite parent, int style) 
	{
		super(page, parent, style, "Vendor");
		this.fadeableComposite = new FadeableComposite(getContainer(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.vendorEditComposite = new LegalEntityEditComposite(fadeableComposite, SWT.NONE, this, false);
		this.vendorEditComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		vendorEditComposite.addLegalEntityValueChangedListener( 
				new ILegalEntityValueChangedListener()
				{
					public void legalEntityValueChanged()
					{
						// if value has changed 				
						markDirty();
					}
				});

//		getSection().setBackgroundMode(SWT.INHERIT_FORCE);
//		getToolBarManager().getControl().setBackgroundMode(SWT.INHERIT_FORCE);
//		getToolBarManager().getControl().setBackground(getSection().getTitleBarGradientBackground());
		inheritAction = new InheritAction();
		registerAction(inheritAction);
		updateToolBarManager();
	}

	public LegalEntityEditComposite getVendorEditComposite() {
		return vendorEditComposite;
	}

	public ProductType getProductType() {
		return productType;
	}

	private AbstractProductTypePageController<ProductType> productTypePageController;



	public void setProductTypeController(AbstractProductTypePageController<ProductType> pageController)
	{
		if (pageController == null || getSection() == null || getSection().isDisposed())
			return;

		productTypePageController = pageController; 

		this.productType = pageController.getProductType();
		getVendorEditComposite().setLegalEntity(pageController.getProductType().getVendor());


	}

	public AbstractProductTypePageController<ProductType> getProductTypeController()
	{

		return productTypePageController;

	}


	/**
	 * sets the {@link ProductType}
	 * @param productType the {@link ProductType} to set
	 */
	public void setProductType(ProductType productType) 
	{
		if (productType == null || getSection() == null || getSection().isDisposed())
			return;

		this.productType = productType;
		getVendorEditComposite().setLegalEntity(productType.getOwner());
	}

	@Override
	public void commit(boolean save) {
		productType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(inheritAction.isChecked()); //$NON-NLS-1$

		if (vendorEditComposite != null && isDirty())
		{
			productType.setOwner(vendorEditComposite.getLegalEntity());
		}

		// delegate itself was already set
		//	productType.getProductTypeLocal().setLocalAccountantDelegate(moneyFlowConfigComposite.getProductTypeMappingTree().getDelegate());
		super.commit(save);
	}

	protected void inheritPressed() {
		if( inheritAction.isChecked() )
		{
			FadeableCompositeJob job = new FadeableCompositeJob("Loading extended product type", fadeableComposite, null) {
				@Override
				protected IStatus run(ProgressMonitor monitor, Object source)
						throws Exception
				{
					final ProductType extendedProductType = productTypePageController.getExtendedProductType(monitor); // since this monitor is not yet started, we can directly pass it

					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{
							if (extendedProductType != null)
								getVendorEditComposite().setLegalEntity(extendedProductType.getOwner());
						}
					});

					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

	private class InheritAction 
	extends InheritanceAction
	{
		@Override
		public void run() {
			if (productType == null)
				return;

			inheritPressed();

			updateToolBarManager();
			markDirty();
		}

		public void updateState(ProductType productType) {
			setChecked(productType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").isValueInherited()); //$NON-NLS-1$
		}
	}

}