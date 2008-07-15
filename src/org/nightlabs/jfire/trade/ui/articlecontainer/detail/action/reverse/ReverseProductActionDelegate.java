package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.eclipse.jface.action.IAction;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ReverseProductActionDelegate 
extends LSDWorkbenchWindowActionDelegate 
{
	private ReverseProductAction reverseProductAction = null;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) 
	{
		getReverseProductAction().run();
	}

	private ReverseProductAction getReverseProductAction() {
		if (reverseProductAction == null) {
			reverseProductAction = new ReverseProductAction(getWindow().getShell());
		}
		return reverseProductAction;
	}
}
