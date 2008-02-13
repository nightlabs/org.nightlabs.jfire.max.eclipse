/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.search;

import java.util.ArrayList;
import java.util.Collection;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Abstract implementation of an {@link QuickSearchEntry} for
 * {@link ArticleContainer}s
 * 
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public abstract class AbstractArticleContainerQuickSearchEntry
extends AbstractQuickSearchEntry
{
	public AbstractArticleContainerQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	/**
	 * return the {@link AbstractArticleContainerQuickSearchQuery} used for searching
	 * @return the {@link AbstractArticleContainerQuickSearchQuery} used for searching
	 */
	public abstract AbstractArticleContainerQuickSearchQuery getQuery();

	/**
	 * return the fetchGroups needed for the query
	 * @return the fetchGroups needed for the query
	 */
	public abstract String[] getFetchGroups();
	
	public Object search(ProgressMonitor monitor)
	{
		Collection<JDOQuery> queries = new ArrayList<JDOQuery>();
		AbstractArticleContainerQuickSearchQuery query = getQuery();
		query.setFromInclude(getMinIncludeRange());
		query.setToExclude(getMaxExcludeRange());
		queries.add(query);
		return ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(queries,
				getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
}
