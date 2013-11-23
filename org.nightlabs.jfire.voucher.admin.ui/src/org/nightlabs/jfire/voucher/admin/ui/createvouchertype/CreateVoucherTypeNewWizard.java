package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class CreateVoucherTypeNewWizard 
extends CreateVoucherTypeWizard 
implements INewWizard
{
	public CreateVoucherTypeNewWizard() {
		super(null);
	}

	public void addRemainingPages() {
		super.addPages();
	}
	
	@Override
	public void addPages() {
		VoucherTypeSelectPage selectPage = new VoucherTypeSelectPage();
		addPage(selectPage);
	}
	
	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// do nothing!!!!
	}
}