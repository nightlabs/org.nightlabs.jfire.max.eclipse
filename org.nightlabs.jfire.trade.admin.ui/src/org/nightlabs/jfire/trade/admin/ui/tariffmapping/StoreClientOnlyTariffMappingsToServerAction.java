package org.nightlabs.jfire.trade.admin.ui.tariffmapping;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class StoreClientOnlyTariffMappingsToServerAction
		implements IViewActionDelegate
{
	private TariffMappingView tariffMappingView;

	public void init(IViewPart view)
	{
		tariffMappingView = (TariffMappingView) view;
	}

	public void run(IAction action)
	{
		tariffMappingView.storeClientOnlyTariffMappingsToServer();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}
}
