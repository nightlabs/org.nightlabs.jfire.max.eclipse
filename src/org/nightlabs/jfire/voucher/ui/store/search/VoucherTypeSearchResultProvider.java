package org.nightlabs.jfire.voucher.ui.store.search;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchResultProvider;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSearchResultProvider
extends AbstractProductTypeSearchResultProvider<VoucherType>
{
	/**
	 * @param factory
	 */
	public VoucherTypeSearchResultProvider(ISearchResultProviderFactory<VoucherType> factory) {
		super(factory);
	}

	@Override
	protected AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell) {
		return new VoucherTypeSearchDialog(shell);
	}

}
