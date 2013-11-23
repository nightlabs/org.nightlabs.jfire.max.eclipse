package org.nightlabs.jfire.dynamictrade.ui.store.search;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchResultProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeSearchResultProvider
extends AbstractProductTypeSearchResultProvider<DynamicProductType>
{
	/**
	 * @param factory
	 */
	public DynamicProductTypeSearchResultProvider(ISearchResultProviderFactory<DynamicProductType> factory) {
		super(factory);
	}

	@Override
	protected AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell) {
		return new DynamicProductTypeSearchDialog(shell);
	}

}
