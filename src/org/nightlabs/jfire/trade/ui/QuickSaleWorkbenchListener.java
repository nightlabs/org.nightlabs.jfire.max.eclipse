package org.nightlabs.jfire.trade.ui;

import org.nightlabs.base.ui.app.IWorkbenchListener;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class QuickSaleWorkbenchListener
implements IWorkbenchListener
{
	public QuickSaleWorkbenchListener() {
	}

	public void openWindows() {
		QuickSalePerspective.checkPerspectiveListenerAdded();
		QuickSalePerspective.checkOrderOpen(RCPUtil.getActivePerspectiveID());
	}

	public void postShutdown() {

	}

	public void postStartup() {
		QuickSalePerspective.checkPerspectiveListenerAdded();
		QuickSalePerspective.checkOrderOpen(RCPUtil.getActivePerspectiveID());
	}

	public void preShutdown() {

	}

	public void preStartup() {

	}

}
