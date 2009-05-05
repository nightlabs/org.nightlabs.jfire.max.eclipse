package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectLocalAccountantDelegatePage;
import org.nightlabs.jfire.voucher.store.VoucherType;

public class AccountVoucherTypeWizard extends DynamicPathWizard{


	private ProductTypeID parentVoucherTypeID;
	private VoucherType voucherType;
	
	public AccountVoucherTypeWizard(ProductTypeID parentVoucherTypeID,VoucherType vouchertype)
	{
		this.voucherType =  vouchertype;
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
		
		VoucherLocalAccountantDelegate voucherLocalAccountantDelegate = null;
		boolean inherit = false;
		switch (selectLocalAccountantDelegatePage.getMode()) {
			case INHERIT:
				voucherLocalAccountantDelegate = selectLocalAccountantDelegatePage.getInheritedLocalAccountantDelegate();
				inherit = true;
				break;
			case CREATE:				
				voucherLocalAccountantDelegate = selectLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate();
				break;
			case SELECT:
				voucherLocalAccountantDelegate = selectLocalAccountantDelegatePage.getSelectedLocalAccountantDelegate();
			default:
				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}
		
		voucherType.getProductTypeLocal().setLocalAccountantDelegate(voucherLocalAccountantDelegate);
		voucherType.getFieldMetaData(
				ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(inherit);
		
		return true;	
	}

}
