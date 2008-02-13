package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;


/**
 * @author Fitas [at] NightLabs [dot] de
 *
 */





public class OwnerVendorConfigSection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{


	private InheritAction inheritAction;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */

	private OwnerVendorConfigComposite ownerVendorConfigComposite = null;


	public OwnerVendorConfigSection(IFormPage page,
			Composite parent, int style)
	{


		super(page, parent, style, "Owner & Vendor");
		this.ownerVendorConfigComposite = new OwnerVendorConfigComposite(getContainer(),
				SWT.NONE, this, false);


		ownerVendorConfigComposite.addLegalEntityValueChangedListener(
				new ILegalEntityValueChangedListener()
		  {
			public void legalEntityValueChanged()
			{
				// if value has changed
					markDirty();
				
			}
		});

		
		
		
		

		getSection().setBackgroundMode(SWT.INHERIT_FORCE);
		getToolBarManager().getControl().setBackgroundMode(SWT.INHERIT_FORCE);
		getToolBarManager().getControl().setBackground(getSection().getTitleBarGradientBackground());


		inheritAction = new InheritAction();
		registerAction(inheritAction);


		updateToolBarManager();
	}



	public OwnerVendorConfigComposite getOwnerVendorConfigComposite() {
		return ownerVendorConfigComposite;
	}


	private ProductType productType = null;


	public ProductType getProductType() {
		return productType;
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
		getOwnerVendorConfigComposite().setProductType(productType);

	}





	@Override
	public void commit(boolean save) {
		productType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(inheritAction.isChecked()); //$NON-NLS-1$



		if (ownerVendorConfigComposite != null && isDirty())
		{
			productType.setOwner(ownerVendorConfigComposite.getOwnerLegalEntity());

			productType.setVendor(ownerVendorConfigComposite.getVendorLegalEntity());
		}


		// delegate itself was already set
		//	productType.getProductTypeLocal().setLocalAccountantDelegate(moneyFlowConfigComposite.getProductTypeMappingTree().getDelegate());
		super.commit(save);
	}




	protected void inheritPressed() {
		if( inheritAction.isChecked() )
		{
		
			
			
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









