package org.nightlabs.jfire.trade.admin.ui.tariffuserset;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.dao.TariffDAO;
import org.nightlabs.jfire.entityuserset.ui.EntityUserSetPageControllerHelper;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffUserSetPageControllerHelper 
extends EntityUserSetPageControllerHelper<Tariff> 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.EntityUserSetPageControllerHelper#getEntityFetchGroups()
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {FetchPlan.DEFAULT, Tariff.FETCH_GROUP_NAME};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.EntityUserSetPageControllerHelper#loadEntities(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected Collection<Tariff> loadEntities(ProgressMonitor monitor) {
		return TariffDAO.sharedInstance().getTariffs(getEntityFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

}
