package org.nightlabs.jfire.dynamictrade.store.search;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeSearchResultProviderFactory 
extends AbstractSearchResultProviderFactory 
{
	public DynamicProductTypeSearchResultProviderFactory() {
	}

	public ISearchResultProvider createSearchResultProvider() {
		return new DynamicProductTypeSearchResultProvider(this);
	}

	public Class getResultTypeClass() {
		return DynamicProductType.class;
	}

}
