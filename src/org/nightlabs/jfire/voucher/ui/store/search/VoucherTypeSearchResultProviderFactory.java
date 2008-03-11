package org.nightlabs.jfire.voucher.ui.store.search;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSearchResultProviderFactory
extends AbstractSearchResultProviderFactory<VoucherType>
{
	@Override
	public ISearchResultProvider<VoucherType> createSearchResultProvider() {
		return new VoucherTypeSearchResultProvider(this);
	}

	@Override
	public Class<VoucherType> getResultTypeClass() {
		return VoucherType.class;
	}

}
