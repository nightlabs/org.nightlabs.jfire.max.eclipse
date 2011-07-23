package org.nightlabs.jfire.trade.ui;

import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.login.ui.LoginStateChangeEvent;
import org.nightlabs.jfire.base.login.ui.LoginStateListener;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.notification.NotificationEvent;

public class LoginStateListenerForProductTypeSelection
implements LoginStateListener
{
	private boolean loggingOut = false;

	@Override
	public void loginStateChanged(LoginStateChangeEvent event)
	{
		if (event.getNewLoginState() == LoginState.ABOUT_TO_LOG_OUT) {
			loggingOut = true;
			// ensure that the classes are loaded before status LOGGED_OUT (in case nothing worked with the class ProductType before).
			ProductType.class.getName();
			ProductTypeID.class.getName();
		}
		else if (loggingOut && event.getNewLoginState() == LoginState.LOGGED_OUT) {
			loggingOut = false;

			SelectionManager.sharedInstance().notify(
					new NotificationEvent(this,
							TradePlugin.ZONE_SALE, null,
							ProductType.class)
			);
			SelectionManager.sharedInstance().notify(
					new NotificationEvent(this,
							TradePlugin.ZONE_SALE, null,
							ProductTypeID.class)
			);
		}
	}
}
