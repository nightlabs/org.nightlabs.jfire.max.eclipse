package org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class CreateDynamicProductTypeNewWizard 
extends CreateDynamicProductTypeWizard 
implements INewWizard
{
	public CreateDynamicProductTypeNewWizard() {
		super(null);
	}

	public void addRemainingPages() {
		super.addPages();
	}
	
	@Override
	public void addPages() {
		DynamicProductTypeSelectPage selectPage = new DynamicProductTypeSelectPage();
		addPage(selectPage);
	}
	
	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// do nothing!!!!
	}
}