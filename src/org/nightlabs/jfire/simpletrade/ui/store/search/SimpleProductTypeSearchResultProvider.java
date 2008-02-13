package org.nightlabs.jfire.simpletrade.ui.store.search;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchResultProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchResultProvider
extends AbstractProductTypeSearchResultProvider
{
	/**
	 * @param factory
	 */
	public SimpleProductTypeSearchResultProvider(ISearchResultProviderFactory factory) {
		super(factory);
	}

	@Override
	protected AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell) {
		return new SimpleProductTypeSearchDialog(shell);
	}

}
