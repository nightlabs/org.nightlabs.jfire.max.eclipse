package org.nightlabs.jfire.trade.admin.ui.tariff;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class MoveTariffUpwardsActionDelegate implements IViewActionDelegate {
	private TariffEditView view;

	public void init(IViewPart arg0) {
		this.view = (TariffEditView) arg0;
	}

	public void run(IAction arg0) {
		view.getTariffListComposite().moveSelectedTariffOneUp();
	}

	public void selectionChanged(IAction arg0, ISelection arg1) {

	}
}
