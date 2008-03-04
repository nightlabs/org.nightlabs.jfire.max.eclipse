package org.nightlabs.jfire.trade.ui.articlecontainer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.query.AbstractJDOQuery;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.jdo.BaseJDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.progress.ProgressMonitor;

public class OfferDAO
		extends BaseJDOObjectDAO<OfferID, Offer>
{
	private static OfferDAO _sharedInstance;
	public static OfferDAO sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new OfferDAO();

		return _sharedInstance;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Implement
	protected Collection<Offer> retrieveJDOObjects(Set<OfferID> offerIDs,
			String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
			throws Exception
	{
		TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		return tm.getOffers(offerIDs, fetchGroups, maxFetchDepth);
	}

	public Offer getOffer(OfferID offerID, String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObject(null, offerID, fetchGroups, maxFetchDepth, monitor);
	}

	public List<Offer> getOffers(Set<OfferID> offerIDs, String[] fetchGroups, int maxFetchDepth, ProgressMonitor monitor)
	{
		return getJDOObjects(null, offerIDs, fetchGroups, maxFetchDepth, monitor);
	}
	
	public Collection<Offer> getOffersByQuery(
		QueryCollection<? extends Offer, ? extends AbstractJDOQuery<? extends Offer>> queries, 
			String[] fetchGroups,	int maxFetchDepth, ProgressMonitor monitor)
//		throws Exception
	{
		try
		{
			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			Set<OfferID> offerIDs = tm.getOfferIDs(queries);
			
			return getJDOObjects(null, offerIDs, fetchGroups, maxFetchDepth, monitor);
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot fetch Offers via Queries.", e);
		}
	}
}
