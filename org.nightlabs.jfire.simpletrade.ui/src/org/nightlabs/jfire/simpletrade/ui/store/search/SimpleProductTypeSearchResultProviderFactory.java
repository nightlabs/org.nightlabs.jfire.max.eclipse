package org.nightlabs.jfire.simpletrade.ui.store.search;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchResultProviderFactory
extends AbstractSearchResultProviderFactory<SimpleProductType>
{
	@Override
	public ISearchResultProvider<SimpleProductType> createSearchResultProvider() {
		return new SimpleProductTypeSearchResultProvider(this);
	}

	@Override
	public Class<SimpleProductType> getResultTypeClass() {
		return SimpleProductType.class;
	}
}
