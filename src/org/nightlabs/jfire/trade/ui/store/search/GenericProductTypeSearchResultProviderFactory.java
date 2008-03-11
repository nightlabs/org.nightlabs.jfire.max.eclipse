package org.nightlabs.jfire.trade.ui.store.search;

import java.util.Locale;

import org.nightlabs.base.ui.search.AbstractSearchResultProviderFactory;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeSearchResultProviderFactory
extends AbstractSearchResultProviderFactory<ProductType>
{
	public GenericProductTypeSearchResultProviderFactory() {
		getName().setText(Locale.ENGLISH.getLanguage(), "Products"); //$NON-NLS-1$
		getName().setText(Locale.GERMANY.getLanguage(), "Produkte"); //$NON-NLS-1$
	}
	
	@Override
	public ISearchResultProvider<ProductType> createSearchResultProvider() {
		return new GenericProductTypeSearchResultProvider(this);
	}

	@Override
	public Class<ProductType> getResultTypeClass() {
		return ProductType.class;
	}
}
