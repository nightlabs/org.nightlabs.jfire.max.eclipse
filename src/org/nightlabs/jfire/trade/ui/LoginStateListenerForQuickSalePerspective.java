package org.nightlabs.jfire.trade.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.LoginStateChangeEvent;
import org.nightlabs.jfire.base.ui.login.LoginStateListener;

public class LoginStateListenerForQuickSalePerspective
implements LoginStateListener
{
	@Override
	public void afterLoginStateChange(LoginStateChangeEvent event)
	{		
		// TODO: is also called at application shutdown (e.g. classloading configuration has changed)
		// and leads to the fact that the AbstractApplication$ExitThread kills the app after 60s
		// and therefore the application do NOT restarts.
		if (event.getNewLoginState() == LoginState.LOGGED_IN)
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
					logger.info("activePerspectiveID is null. Will re-enqueue this method into the event dispatcher and exit."); //$NON-NLS-1$
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// ignore
					}
					checkOrderOpenAsynchronously();
					return;
				}

				logger.info("Calling QuickSalePerspective.checkOrderOpen(...) with activePerspectiveID=" + activePerspectiveID); //$NON-NLS-1$
				QuickSalePerspective.checkOrderOpen(activePerspectiveID);
			}
		});
	}


	@Override
	public void beforeLoginStateChange(LoginStateChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
