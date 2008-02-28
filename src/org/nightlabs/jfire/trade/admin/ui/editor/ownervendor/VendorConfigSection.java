package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerConfigSection.InheritAction;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;


/**
 * @author Fitas [at] NightLabs [dot] de
 *
 */
public class VendorConfigSection  
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private InheritAction inheritAction;
	private LegalEntityEditComposite vendorEditComposite = null;
	private ProductType productType = null;

	public VendorConfigSection(IFormPage page,
			Composite parent, int style) 
	{
		super(page, parent, style, "Vendor");
		this.vendorEditComposite = new LegalEntityEditComposite(getContainer(), 
				SWT.NONE, this, false);
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
			ProductType inProductType =	productTypePageController.getExtendedProductType(new NullProgressMonitor());
			
			getVendorEditComposite().setLegalEntity(inProductType.getOwner());
		
		}
	}	
			class InheritAction 
			extends InheritanceAction {
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