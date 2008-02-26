package org.nightlabs.jfire.trade.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.LoginStateListener;

public class LoginStateListenerForQuickSalePerspective
implements LoginStateListener
{
	@Override
	public void loginStateChanged(int loginState, IAction action)
	{
		if (loginState == Login.LOGINSTATE_LOGGED_IN)
			checkOrderOpenAsynchronously();
	}

	private void checkOrderOpenAsynchronously()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				Logger logger = Logger.getLogger(LoginStateListenerForQuickSalePerspective.class);

				String activePerspectiveID = RCPUtil.getActivePerspectiveID();
				if (activePerspectiveID == null) {
					logger.info("activePerspectiveID is null. Will re-enqueue this method into the event dispatcher and exit.");
					checkOrderOpenAsynchronously();
					return;
				}

				logger.info("Calling QuickSalePerspective.checkOrderOpen(...) with activePerspectiveID=" + activePerspectiveID);
				QuickSalePerspective.checkOrderOpen(activePerspectiveID);
			}
		});
	}
}
