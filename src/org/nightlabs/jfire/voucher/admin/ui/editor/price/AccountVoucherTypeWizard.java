package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage;

public class AccountVoucherTypeWizard extends DynamicPathWizard{


	private ProductTypeID parentVoucherTypeID;
	
	public AccountVoucherTypeWizard(ProductTypeID parentVoucherTypeID)
	{
		this.parentVoucherTypeID = parentVoucherTypeID;
	}

	private SelectLocalAccountantDelegatePage selectLocalAccountantDelegatePage;

	@Override
	public void addPages()
	{
		selectLocalAccountantDelegatePage = new SelectLocalAccountantDelegatePage(parentVoucherTypeID);
		addPage(selectLocalAccountantDelegatePage);
	}
	
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
