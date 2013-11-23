package org.nightlabs.jfire.department.admin.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateDepartmentAction 
implements IViewActionDelegate 
{
	public static final String ID = CreateDepartmentAction.class.getName();
	
	private IViewPart viewPart;
	@Override
	public void init(IViewPart viewPart) {
		this.viewPart = viewPart;
	}

	@Override
	public void run(IAction action) {
		CreateDepartmentDialog createDepartmentDialog = new CreateDepartmentDialog(viewPart.getSite().getShell());
		createDepartmentDialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}
}
