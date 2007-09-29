package org.nightlabs.jfire.trade.ui.tariffmapping;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.TariffMapping;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.accounting.id.TariffMappingID;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;

public class TariffMappingDAO
extends JDOObjectDAO<TariffMappingID, TariffMapping>
{
	private static TariffMappingDAO sharedInstance = null;

	public static TariffMappingDAO sharedInstance()
	{
		if (sharedInstance == null) {
			synchronized (TariffMappingDAO.class) {
				if (sharedInstance == null)
					sharedInstance = new TariffMappingDAO();
			}
		}
		return sharedInstance;
	}

	public TariffMappingDAO() { }

	@Implement
	protected Collection<TariffMapping> retrieveJDOObjects(
			Set<TariffMappingID> tariffMappingIDs, String[] fetchGroups, int maxFetchDepth,
			IProgressMonitor monitor)
			throws Exception
	{
		AccountingManager am = accountingManager;
		if (am == null)
			am = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

		return am.getTariffMappings(tariffMappingIDs, fetchGroups, maxFetchDepth);
	}

	private AccountingManager accountingManager;

	public synchronized List<TariffMapping> getTariffMappings(String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		try {
			accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			try {
				Set<TariffMappingID> tariffMappingIDs = accountingManager.getTariffMappingIDs();
				return getJDOObjects(null, tariffMappingIDs, fetchGroups, maxFetchDepth, monitor);
			} finally {
				accountingManager = null;
			}
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public List<TariffMapping> getTariffMappings(Set<TariffMappingID> tariffMappingIDs, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		return getJDOObjects(null, tariffMappingIDs, fetchGroups, maxFetchDepth, monitor);
	}

	public TariffMapping createTariffMapping(TariffID partnerTariffID, TariffID localTariffID, boolean get, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	{
		try {
			AccountingManager am = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			TariffMapping tm = am.createTariffMapping(partnerTariffID, localTariffID, get, fetchGroups, maxFetchDepth);

			if (tm != null)
				Cache.sharedInstance().put(null, tm, fetchGroups, maxFetchDepth);

			return tm;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
}
