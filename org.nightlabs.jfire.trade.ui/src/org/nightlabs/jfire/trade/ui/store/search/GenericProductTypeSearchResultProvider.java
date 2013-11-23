package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeSearchResultProvider
extends AbstractProductTypeSearchResultProvider<ProductType>
{
	/**
	 * @param factory
	 */
	public GenericProductTypeSearchResultProvider(ISearchResultProviderFactory<ProductType> factory) {
		super(factory);
	}

	@Override
	protected AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell) {
		return new GenericProductTypeSearchDialog(shell);
	}

}
