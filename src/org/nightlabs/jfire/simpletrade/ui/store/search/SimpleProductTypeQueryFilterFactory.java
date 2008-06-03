/**
 * 
 */
package org.nightlabs.jfire.simpletrade.ui.store.search;

import org.nightlabs.jfire.simpletrade.store.search.SimpleProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class SimpleProductTypeQueryFilterFactory 
extends AbstractProductTypeSearchQueryFilterFactory<SimpleProductTypeQuery> 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory#getQueryClass()
	 */
	@Override
	protected Class<SimpleProductTypeQuery> getQueryClass() {
		return SimpleProductTypeQuery.class;
	}
}
