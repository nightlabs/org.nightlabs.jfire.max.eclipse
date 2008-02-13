package org.nightlabs.jfire.simpletrade.ui.store.search;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.simpletrade.store.search.SimpleProductTypeQuery;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.search.ProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchComposite
extends AbstractProductTypeSearchComposite
{
	/**
	 * @param parent
	 * @param style
	 */
	public SimpleProductTypeSearchComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected ProductTypeQuery createNewQuery() {
		return new SimpleProductTypeQuery();
	}

	@Override
	protected String[] getFetchGroups()
	{
		Set<String> fetchGroups = CollectionUtil.array2HashSet(super.getFetchGroups());
		fetchGroups.add(ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS);
		return CollectionUtil.collection2TypedArray(fetchGroups, String.class);
	}
}
