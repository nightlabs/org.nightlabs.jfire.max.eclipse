package org.nightlabs.jfire.voucher.admin.ui.editor.accountpriceconfig;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage;

public class AccountVoucherTypeWizard extends DynamicPathWizard{


	private ProductTypeID parentVoucherTypeID;
	private VoucherLocalAccountantDelegate selectedWizardVoucherLocalAccountantDelegate;
	private SelectLocalAccountantDelegatePage selectLocalAccountantDelegatePage;
	private Boolean inherit = false;	
	public AccountVoucherTypeWizard(ProductTypeID parentVoucherTypeID)
	{
		this.parentVoucherTypeID = parentVoucherTypeID;
	}



	@Override
	public void addPages()
	{
		selectLocalAccountantDelegatePage = new SelectLocalAccountantDelegatePage(parentVoucherTypeID);
		addPage(selectLocalAccountantDelegatePage);
	}
	
	Boolean isInherited()
	{
		return inherit;
	}
	VoucherLocalAccountantDelegate selectedVoucherLocalAccountantDelegate()
	{
		return this.selectedWizardVoucherLocalAccountantDelegate;
	}
	
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub

		switch (selectLocalAccountantDelegatePage.getMode()) {
			case INHERIT:
				selectedWizardVoucherLocalAccountantDelegate = selectLocalAccountantDelegatePage.getInheritedLocalAccountantDelegate();
				this.inherit = true;
				break;
			case CREATE:				
				selectedWizardVoucherLocalAccountantDelegate = selectLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate();
				break;
			case SELECT:
				selectedWizardVoucherLocalAccountantDelegate = selectLocalAccountantDelegatePage.getSelectedLocalAccountantDelegate();
				break;
			default:
				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}
		
		
		return true;	
	}

}
