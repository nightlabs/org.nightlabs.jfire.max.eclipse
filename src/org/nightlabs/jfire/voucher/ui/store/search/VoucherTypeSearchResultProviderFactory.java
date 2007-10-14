package org.nightlabs.jfire.voucher.ui.store.search;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSearchResultProviderFactory 
extends AbstractSearchResultProviderFactory 
{
	public VoucherTypeSearchResultProviderFactory() {
	}

	public ISearchResultProvider createSearchResultProvider() {
		return new VoucherTypeSearchResultProvider(this);
	}

	public Class getResultTypeClass() {
		return VoucherType.class;
	}

}
