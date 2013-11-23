package org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.action;

import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.dashboard.ui.action.AbstractDashboardTableAction;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.TransactionInfoTableItem;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.TradePerspective;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assigncustomer.AssignCustomerAction;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author abieber
 *
 */
public class SelectCustomerAction extends AbstractDashboardTableAction<TransactionInfoTableItem> {

	public SelectCustomerAction() {
		setId(SelectCustomerAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.action.SelectCustomerAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				TradePlugin.getDefault(), AssignCustomerAction.class));
	}
	
	@Override
	public void run() {
		TransactionInfoTableItem tableItem = getFirstSelectedTableItem();
		if (tableItem != null) {
			RCPUtil.showPerspective(TradePerspective.ID_PERSPECTIVE);
			NotificationEvent event = new NotificationEvent(this, TradePlugin.ZONE_SALE, tableItem.getTransactionInfo().getCustomerID(), 
				LegalEntity.class);
			SelectionManager.sharedInstance().notify(event);
		}
	}
	
	@Override
	public boolean calculateEnabled() {
		return getFirstSelectedTableItem() != null;
	}
}
