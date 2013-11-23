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
public abstract class AbstractArticleContainerQuickSearchEntry<Q extends AbstractArticleContainerQuery>
	extends AbstractQuickSearchEntry<Q>
{
	public AbstractArticleContainerQuickSearchEntry(QuickSearchEntryFactory<Q> factory,
		Class<Q> queryType)
	{
		super(factory, queryType);
	}
}
