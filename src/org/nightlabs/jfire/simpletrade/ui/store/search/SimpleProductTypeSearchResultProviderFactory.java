package org.nightlabs.jfire.simpletrade.ui.store.search;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchResultProviderFactory
extends AbstractSearchResultProviderFactory
{
	public SimpleProductTypeSearchResultProviderFactory() {
	}

	public ISearchResultProvider createSearchResultProvider() {
		return new SimpleProductTypeSearchResultProvider(this);
	}

	public Class getResultTypeClass() {
		return SimpleProductType.class;
	}

}
