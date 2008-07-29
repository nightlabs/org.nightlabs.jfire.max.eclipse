package org.nightlabs.jfire.trade.ui.articlecontainer.recurring;

import java.util.Collection;
import java.util.Set;
import java.util.List;

import org.nightlabs.jfire.base.jdo.BaseJDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Order;

import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.recurring.RecurringTradeManager;
import org.nightlabs.jfire.trade.recurring.RecurringTradeManagerUtil;


import org.nightlabs.progress.ProgressMonitor;

public class RecurringOrderDAO 
extends BaseJDOObjectDAO<OrderID, RecurringOrder>
{
	private static  RecurringOrderDAO sharedInstance = null;

	public static RecurringOrderDAO sharedInstance()
	{
		if (sharedInstance == null) {
			synchronized (RecurringOrderDAO.class) {
				if (sharedInstance == null)
					sharedInstance = new RecurringOrderDAO();
			}
		}
		return sharedInstance;
	}

	@Override
	protected Collection<RecurringOrder> retrieveJDOObjects(Set<OrderID> orderIDs,
			String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
			throws Exception
	{
		
		RecurringTradeManager tm = RecurringTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		return tm.getOrders(orderIDs, fetchGroups, maxFetchDepth);
	}
	
	public Order getOrder(OrderID orderID, String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObject(null, orderID, fetchGroups, maxFetchDepth, monitor);
	}

	public List<RecurringOrder> getOrders(Set<OrderID> orderIDs, String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObjects(null, orderIDs, fetchGroups, maxFetchDepth, monitor);
	}
		
	
	protected RecurringOrderDAO() {}

	
}
