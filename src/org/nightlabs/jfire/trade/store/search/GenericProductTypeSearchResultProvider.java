package org.nightlabs.jfire.trade.store.search;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeSearchResultProvider 
extends AbstractProductTypeSearchResultProvider 
{
	/**
	 * @param factory
	 */
	public GenericProductTypeSearchResultProvider(ISearchResultProviderFactory factory) {
		super(factory);
	}

	@Override
	protected AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell) {
		return new GenericProductTypeSearchDialog(shell);
	}

}
