/**
 * 
 */
package org.nightlabs.jfire.trade.articlecontainer.detail.action.quicksale;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.QuickSalePerspective;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class CreateQuickSaleOrder 
implements IWorkbenchWindowActionDelegate 
{

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {

	}

	public void run(IAction action) {
		QuickSalePerspective.checkOrderOpen(RCPUtil.getActivePerspectiveID());
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
