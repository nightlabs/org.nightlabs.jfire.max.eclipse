package org.nightlabs.jfire.trade.articlecontainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.base.jdo.BaseJDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

public class OrderDAO
		extends BaseJDOObjectDAO<OrderID, Order>
{
	private static OrderDAO sharedInstance = null;

	public static OrderDAO sharedInstance()
	{
		if (sharedInstance == null) {
			synchronized (OrderDAO.class) {
				if (sharedInstance == null)
					sharedInstance = new OrderDAO();
			}
		}
		return sharedInstance;
	}

	protected OrderDAO() {}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Implement
	protected Collection<Order> retrieveJDOObjects(Set<OrderID> orderIDs,
			String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
			throws Exception
	{
		TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		return tm.getOrders(orderIDs, fetchGroups, maxFetchDepth);
	}

	public Order getOrder(OrderID orderID, String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObject(null, orderID, fetchGroups, maxFetchDepth, monitor);
	}

	public List<Order> getOrders(Set<OrderID> orderIDs, String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObjects(null, orderIDs, fetchGroups, maxFetchDepth, monitor);
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List<Order> getOrders(
			AnchorID vendorID, AnchorID customerID, long rangeBeginIdx, long rangeEndIdx,
			String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		try {
			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			List<OrderID> orderIDList = tm.getOrderIDs(vendorID, customerID, rangeBeginIdx, rangeEndIdx);
			Set<OrderID> orderIDs = new HashSet<OrderID>(orderIDList);

			Map<OrderID, Order> orderMap = new HashMap<OrderID, Order>(orderIDs.size());
			for(Order order : getJDOObjects(null, orderIDs, fetchGroups, maxFetchDepth, monitor))
				orderMap.put((OrderID) JDOHelper.getObjectId(order), order);

			List<Order> res = new ArrayList<Order>(orderIDList.size());
			for (OrderID orderID : orderIDList) {
				Order order = orderMap.get(orderID);
				if (order != null)
					res.add(order);
			}

			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
