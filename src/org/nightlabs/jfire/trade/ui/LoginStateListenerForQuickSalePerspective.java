package org.nightlabs.jfire.trade.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.LoginStateChangeEvent;
import org.nightlabs.jfire.base.ui.login.LoginStateListener;

public class LoginStateListenerForQuickSalePerspective
implements LoginStateListener
{	
	@Override
	public void afterLoginStateChange(LoginStateChangeEvent event)
	{		
		if (event.getNewLoginState() == LoginState.LOGGED_IN)
			checkOrderOpenAsynchronously();
	}

	private void checkOrderOpenAsynchronously()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				final Logger logger = Logger.getLogger(LoginStateListenerForQuickSalePerspective.class);

				String activePerspectiveID = RCPUtil.getActivePerspectiveID();
				if (activePerspectiveID == null) {
					// This Thread is necessary in order to prevent blocking the application from shutdown.
					// It might happen, that the application never has an active perspective, because it is shut down before
					// completely starting (e.g. when the classloader-config changed and the login occurs very early
					// and decides to restart the application.
					Thread thread = new Thread() {
						@Override
						public void run() {
							logger.info("activePerspectiveID is null. Will re-enqueue this method into the event dispatcher and exit."); //$NON-NLS-1$
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// ignore
							}
							checkOrderOpenAsynchronously();
						}
					};
					thread.setDaemon(true);
					thread.start();
					return;
				}

				logger.info("Calling QuickSalePerspective.checkOrderOpen(...) with activePerspectiveID=" + activePerspectiveID); //$NON-NLS-1$
				QuickSalePerspective.checkOrderOpen(activePerspectiveID);
			}
		});
	}

	@Override
	public void beforeLoginStateChange(LoginStateChangeEvent event) {
		// nothing to do here
	}
}
