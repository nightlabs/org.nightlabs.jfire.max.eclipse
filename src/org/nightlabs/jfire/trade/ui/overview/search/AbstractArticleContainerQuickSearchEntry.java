package org.nightlabs.jfire.trade.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;

/**
 * Abstract implementation of an {@link QuickSearchEntry} for
 * {@link ArticleContainer}s
 * 
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public abstract class AbstractArticleContainerQuickSearchEntry<R extends ArticleContainer, Q extends AbstractArticleContainerQuery<R>>
	extends AbstractQuickSearchEntry<R, Q>
{
	public AbstractArticleContainerQuickSearchEntry(QuickSearchEntryFactory<R, Q> factory, Class<Q> queryType) {
		super(factory, queryType);
	}

//	/**
//	 * return the {@link AbstractArticleContainerQuery} used for searching
//	 * @return the {@link AbstractArticleContainerQuery} used for searching
//	 */
//	@Override
//	public abstract AbstractArticleContainerQuery getQuery();

//	/**
//	 * return the fetchGroups needed for the query
//	 * @return the fetchGroups needed for the query
//	 */
//	public abstract String[] getFetchGroups();
	
//	public Object search(ProgressMonitor monitor)
//	{
//		Collection<AbstractJDOQuery> queries = new ArrayList<AbstractJDOQuery>();
//		AbstractArticleContainerQuery query = getQuery();
//		query.setFromInclude(getMinIncludeRange());
//		query.setToExclude(getMaxExcludeRange());
//		queries.add(query);
//		return ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(queries,
//				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//	}
}
