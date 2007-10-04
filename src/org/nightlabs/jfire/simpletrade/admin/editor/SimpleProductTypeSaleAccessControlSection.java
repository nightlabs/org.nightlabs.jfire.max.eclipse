package org.nightlabs.jfire.simpletrade.admin.editor;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.simpletrade.admin.resource.Messages;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractSaleAccessControlSection;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeDetailPage;
import org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper;
import org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlHelper;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSaleAccessControlSection 
extends AbstractSaleAccessControlSection 
{
	public SimpleProductTypeSaleAccessControlSection(IProductTypeDetailPage page, Composite parent, int style) 
	{
		super(
				page, parent, style,
				Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypeSaleAccessControlSection.title")); //$NON-NLS-1$
	}
	
	public SimpleProductTypeSaleAccessControlSection(IProductTypeDetailPage page, Composite parent) {
		super(page, parent);
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
