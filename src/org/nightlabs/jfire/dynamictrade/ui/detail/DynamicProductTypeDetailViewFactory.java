package org.nightlabs.jfire.dynamictrade.ui.detail;

import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.trade.ui.detail.IProductTypeDetailView;
import org.nightlabs.jfire.trade.ui.detail.ProductTypeDetailViewFactory;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author marco schulze - marco at nightlabs dot de
 */
public class DynamicProductTypeDetailViewFactory
implements ProductTypeDetailViewFactory
{

	public IProductTypeDetailView createProductTypeDetailView() {
		return new DynamicProductTypeDetailView();
	}

	public Class<DynamicProductType> getProductTypeClass() {
		return DynamicProductType.class;
	}

}
