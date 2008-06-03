/**
 * 
 */
package org.nightlabs.jfire.trade.ui.store.search;

import org.nightlabs.jfire.store.search.BaseProductTypeQuery;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class BaseProductTypeQueryFilterFactory
extends AbstractProductTypeSearchQueryFilterFactory<BaseProductTypeQuery> 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory#getQueryClass()
	 */
	@Override
	protected Class<BaseProductTypeQuery> getQueryClass() {
		return BaseProductTypeQuery.class;
	}

}
