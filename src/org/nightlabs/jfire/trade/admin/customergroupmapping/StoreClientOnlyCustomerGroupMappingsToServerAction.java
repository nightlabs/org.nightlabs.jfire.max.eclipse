package org.nightlabs.jfire.trade.admin.customergroupmapping;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class StoreClientOnlyCustomerGroupMappingsToServerAction
		implements IViewActionDelegate
{
	private CustomerGroupMappingView tariffMappingView;

	public void init(IViewPart view)
	{
		tariffMappingView = (CustomerGroupMappingView) view;
	}

	public void run(IAction action)
	{
		tariffMappingView.storeClientOnlyCustomerGroupMappingsToServer();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}
}
