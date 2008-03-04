package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.query.RepositoryQuery;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositoryNameQuickSearchEntry
	extends AbstractQuickSearchEntry<Repository, RepositoryQuery>
{
	public RepositoryNameQuickSearchEntry(QuickSearchEntryFactory<Repository, RepositoryQuery> factory)
	{
		super(factory, RepositoryQuery.class);
	}

//	@Override
//	protected void doResetSearchCondition(RepositoryQuery query, String lastValue)
//	{
//		query.setName(lastValue);
//	}

	@Override
	protected void doSetSearchConditionValue(RepositoryQuery query, String value)
	{
		query.setName(value);
	}

	@Override
	protected void doUnsetSearchConditionValue(RepositoryQuery query)
	{
		query.setName(null);
	}

//	public Object search(ProgressMonitor monitor)
//	{
//		RepositoryQuery query = new RepositoryQuery();
//		query.setName(getSearchText());
//		query.setFromInclude(getMinIncludeRange());
//		query.setToExclude(getMaxExcludeRange());
//		return RepositoryDAO.sharedInstance().getRepositoriesForQueries(Collections.singleton(query),
//			RepositoryEntryViewer.FETCH_GROUPS_REPOSITORIES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//			monitor);
//	}
}
