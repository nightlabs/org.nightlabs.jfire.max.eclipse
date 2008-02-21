/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.repository.search;

import java.util.Collections;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.dao.RepositoryDAO;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.ui.overview.repository.RepositoryEntryViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class RepositoryOwnerNameQuickSearchEntry
extends AbstractQuickSearchEntry
{
	public RepositoryOwnerNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	public Object search(ProgressMonitor monitor) {
		RepositoryQuery query = new RepositoryQuery();
		query.setOwnerName(getSearchText());
		query.setFromInclude(getMinIncludeRange());
		query.setToExclude(getMaxExcludeRange());
		return RepositoryDAO.sharedInstance().getRepositoriesForQueries(
				Collections.singleton(query),
				RepositoryEntryViewer.FETCH_GROUPS_REPOSITORIES,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
}
