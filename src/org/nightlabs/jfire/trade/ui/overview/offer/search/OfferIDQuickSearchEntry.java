package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerQuickSearchEntry;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferIDQuickSearchEntry
	extends AbstractArticleContainerQuickSearchEntry<OfferQuery>
{
	public OfferIDQuickSearchEntry(QuickSearchEntryFactory<OfferQuery> factory)
	{
		super(factory, OfferQuery.class);
	}

	@Override
	protected String getModifiedQueryFieldName()
	{
		return AbstractArticleContainerQuery.FieldName.articleContainerID;
	}
}
