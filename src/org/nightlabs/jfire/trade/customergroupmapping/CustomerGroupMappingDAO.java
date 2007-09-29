package org.nightlabs.jfire.trade.customergroupmapping;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.CustomerGroupMapping;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.CustomerGroupMappingID;

public class CustomerGroupMappingDAO
extends JDOObjectDAO<CustomerGroupMappingID, CustomerGroupMapping>
{
	private static CustomerGroupMappingDAO sharedInstance = null;

	public static CustomerGroupMappingDAO sharedInstance()
	{
		if (sharedInstance == null) {
			synchronized (CustomerGroupMappingDAO.class) {
				if (sharedInstance == null)
					sharedInstance = new CustomerGroupMappingDAO();
			}
		}
		return sharedInstance;
	}

	public CustomerGroupMappingDAO() { }

	@Implement
	protected Collection<CustomerGroupMapping> retrieveJDOObjects(
			Set<CustomerGroupMappingID> customerGroupMappingIDs, String[] fetchGroups, int maxFetchDepth,
			IProgressMonitor monitor)
			throws Exception
	{
		TradeManager tm = tradeManager;
		if (tm == null)
			tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

		return tm.getCustomerGroupMappings(customerGroupMappingIDs, fetchGroups, maxFetchDepth);
	}

	private TradeManager tradeManager;

	public synchronized List<CustomerGroupMapping> getCustomerGroupMappings(String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		try {
			tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			try {
				Set<CustomerGroupMappingID> customerGroupMappingIDs = tradeManager.getCustomerGroupMappingIDs();
				return getJDOObjects(null, customerGroupMappingIDs, fetchGroups, maxFetchDepth, monitor);
			} finally {
				tradeManager = null;
			}
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public List<CustomerGroupMapping> getCustomerGroupMappings(Set<CustomerGroupMappingID> customerGroupMappingIDs, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		return getJDOObjects(null, customerGroupMappingIDs, fetchGroups, maxFetchDepth, monitor);
	}

	public CustomerGroupMapping createCustomerGroupMapping(CustomerGroupID partnerCustomerGroupID, CustomerGroupID localCustomerGroupID, boolean get, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		try {
			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			CustomerGroupMapping cgm = tm.createCustomerGroupMapping(partnerCustomerGroupID, localCustomerGroupID, get, fetchGroups, maxFetchDepth);

			if (cgm != null)
				Cache.sharedInstance().put(null, cgm, fetchGroups, maxFetchDepth);

			return cgm;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
}
