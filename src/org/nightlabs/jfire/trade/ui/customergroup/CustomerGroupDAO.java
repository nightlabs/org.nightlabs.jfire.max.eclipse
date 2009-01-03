package org.nightlabs.jfire.trade.ui.customergroup;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.id.CustomerGroupID;

public class CustomerGroupDAO
		extends JDOObjectDAO<CustomerGroupID, CustomerGroup>
{
	private static CustomerGroupDAO sharedInstance = null;

	public static CustomerGroupDAO sharedInstance()
	{
		if (sharedInstance == null) {
			synchronized (CustomerGroupDAO.class) {
				if (sharedInstance == null)
					sharedInstance = new CustomerGroupDAO();
			}
		}
		return sharedInstance;
	}

	@Override
	@Implement
	protected Collection<CustomerGroup> retrieveJDOObjects(
			Set<CustomerGroupID> customerGroupIDs, String[] fetchGroups, int maxFetchDepth,
			IProgressMonitor monitor)
			throws Exception
	{
		TradeManager tm = tradeManager;
		if (tm == null)
			tm = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());

		return tm.getCustomerGroups(customerGroupIDs, fetchGroups, maxFetchDepth);
	}

	private TradeManager tradeManager;

	public List<CustomerGroup> getCustomerGroups(String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		return getCustomerGroups(null, false, fetchGroups, maxFetchDepth, monitor);
	}

	/**
	 * @param organisationID <code>null</code> in order to get all customerGroups (no filtering). non-<code>null</code> to filter by <code>organisationID</code>.
	 * @param inverse This applies only if <code>organisationID != null</code>. If <code>true</code>, it will return all {@link CustomerGroupID}s where the <code>organisationID</code>
	 *		is NOT the one passed as parameter <code>organisationID</code>.
	 * @param fetchGroups
	 * @param maxFetchDepth
	 * @param monitor
	 * @return
	 */
	public synchronized List<CustomerGroup> getCustomerGroups(String organisationID, boolean inverse, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		try {
			tradeManager = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());
			try {
				Set<CustomerGroupID> customerGroupIDs = tradeManager.getCustomerGroupIDs(organisationID, inverse);
				return getJDOObjects(null, customerGroupIDs, fetchGroups, maxFetchDepth, monitor);
			} finally {
				tradeManager = null;
			}
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public List<CustomerGroup> getCustomerGroups(Set<CustomerGroupID> customerGroupIDs, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		return getJDOObjects(null, customerGroupIDs, fetchGroups, maxFetchDepth, monitor);
	}

	public CustomerGroup storeCustomerGroup(CustomerGroup customerGroup, boolean get, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		try {
			TradeManager tm = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());

			CustomerGroup cg = tm.storeCustomerGroup(customerGroup, get, fetchGroups, maxFetchDepth);
			if (cg != null)
				Cache.sharedInstance().put(null, cg, fetchGroups, maxFetchDepth);

			return cg;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
}
