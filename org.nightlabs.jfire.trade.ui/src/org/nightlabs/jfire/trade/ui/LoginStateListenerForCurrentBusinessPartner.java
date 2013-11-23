package org.nightlabs.jfire.trade.ui;

import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.base.login.ui.LoginStateChangeEvent;
import org.nightlabs.jfire.base.login.ui.LoginStateListener;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LoginStateListenerForCurrentBusinessPartner
implements LoginStateListener
{
	@Override
	public void loginStateChanged(LoginStateChangeEvent event)
	{
		if (event.getNewLoginState() == LoginState.LOGGED_IN)
		{
			AnchorID anonymousLegalEntityID = LegalEntityDAO.sharedInstance().getAnonymousLegalEntityID();
			JDOObjectID2PCClassMap.sharedInstance().initPersistenceCapableClass(
					anonymousLegalEntityID, LegalEntity.class
			);
			NotificationEvent notificationEvent = new NotificationEvent(
					this, TradePlugin.ZONE_SALE, anonymousLegalEntityID
			);
			SelectionManager.sharedInstance().notify(notificationEvent);
		}
	}
}
