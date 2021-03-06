package org.nightlabs.jfire.simpletrade.ui.detail;

import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.trade.ui.detail.IProductTypeDetailView;
import org.nightlabs.jfire.trade.ui.detail.ProductTypeDetailViewFactory;

/**
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SimpleProductTypeDetailViewFactory
implements ProductTypeDetailViewFactory
{

	public IProductTypeDetailView createProductTypeDetailView() {
		return new SimpleProductTypeDetailView();
	}

	public Class<SimpleProductType> getProductTypeClass() {
		return SimpleProductType.class;
	}

}
