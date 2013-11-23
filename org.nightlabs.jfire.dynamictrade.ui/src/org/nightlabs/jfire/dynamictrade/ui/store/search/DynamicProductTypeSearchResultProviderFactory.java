package org.nightlabs.jfire.dynamictrade.ui.store.search;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeSearchResultProviderFactory
extends AbstractSearchResultProviderFactory<DynamicProductType>
{
	@Override
	public ISearchResultProvider<DynamicProductType> createSearchResultProvider() {
		return new DynamicProductTypeSearchResultProvider(this);
	}

	@Override
	public Class<DynamicProductType> getResultTypeClass() {
		return DynamicProductType.class;
	}

}
