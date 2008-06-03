/**
 * 
 */
package org.nightlabs.jfire.voucher.ui.store.search;

import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory;
import org.nightlabs.jfire.voucher.store.search.VoucherTypeQuery;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class VoucherTypeQueryFilterFactory 
extends AbstractProductTypeSearchQueryFilterFactory<VoucherTypeQuery> 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchQueryFilterFactory#getQueryClass()
	 */
	@Override
	protected Class<VoucherTypeQuery> getQueryClass() {
		return VoucherTypeQuery.class;
	}
}
