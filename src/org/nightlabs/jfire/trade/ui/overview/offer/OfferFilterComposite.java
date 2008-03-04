package org.nightlabs.jfire.trade.ui.overview.offer;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OfferFilterComposite
	extends AbstractArticleContainerFilterComposite<Offer, OfferQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param queryProvider
	 */
	public OfferFilterComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode, QueryProvider<Offer, ? super OfferQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public OfferFilterComposite(Composite parent, int style,
		QueryProvider<Offer, ? super OfferQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	protected Class<OfferQuery> getQueryClass() {
		return OfferQuery.class;
	}

//	@Override
//	protected ArticleContainerQuery createArticleContainerQuery()
//	{
//		return new OfferQuery();
//	}
}
