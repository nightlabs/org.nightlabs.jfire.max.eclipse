package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractSaleAccessControlSection;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeDetailPage;
import org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper;
import org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlHelper;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class DynamicProductTypeSaleAccessControlSection  
extends AbstractSaleAccessControlSection
{
	public DynamicProductTypeSaleAccessControlSection(IProductTypeDetailPage page, Composite parent, int style) 
	{
		super(page, parent, style);
	}

	@Override
	protected SaleAccessControlHelper createSaleAccessControlHelper() {
		return new AbstractSaleAccessControlHelper() {
//			public Set<String> getFetchGroupsProductType() {
//				return super.getFetchGroupsProductType();
//			}
		};
	}
}
