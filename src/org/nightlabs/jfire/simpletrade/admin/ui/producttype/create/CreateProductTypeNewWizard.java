package org.nightlabs.jfire.simpletrade.admin.ui.producttype.create;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class CreateProductTypeNewWizard 
extends CreateProductTypeWizard 
implements INewWizard
{
	public CreateProductTypeNewWizard() {
		super(null);
	}

	public void addRemainingPages() {
		super.addPages();
	}
	
	@Override
	public void addPages() {
		ProductTypeSelectPage selectPage = new ProductTypeSelectPage();
		addPage(selectPage);
	}
	
	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// do nothing!!!!
	}
}