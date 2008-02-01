package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;



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


		// delegate itself was already set
		//	productType.getProductTypeLocal().setLocalAccountantDelegate(moneyFlowConfigComposite.getProductTypeMappingTree().getDelegate());
		super.commit(save);
	}





	class InheritAction 
	extends InheritanceAction {
		@Override
		public void run() {
			if (productType == null)
				return;

			updateToolBarManager();
			markDirty();
		}

		public void updateState(ProductType productType) {
			setChecked(productType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").isValueInherited()); //$NON-NLS-1$
		}
	}


}









