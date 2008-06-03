/**
 * 
 */
package org.nightlabs.jfire.dynamictrade.ui.store.search;

import org.nightlabs.jfire.dynamictrade.store.search.DynamicProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DynamicProductTypeQueryFilterFactory 
extends AbstractProductTypeSearchQueryFilterFactory<DynamicProductTypeQuery> 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory#getQueryClass()
	 */
	@Override
	protected Class<DynamicProductTypeQuery> getQueryClass() {
		return DynamicProductTypeQuery.class;
	}
}
