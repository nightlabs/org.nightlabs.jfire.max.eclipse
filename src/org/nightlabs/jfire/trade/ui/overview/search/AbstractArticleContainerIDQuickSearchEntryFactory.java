package org.nightlabs.jfire.trade.ui.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public abstract class AbstractArticleContainerIDQuickSearchEntryFactory<R extends ArticleContainer, Q extends AbstractArticleContainerQuickSearchQuery<R>>
	extends AbstractQuickSearchEntryFactory<R, Q>
{
	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntry.name"); //$NON-NLS-1$
	}
}
