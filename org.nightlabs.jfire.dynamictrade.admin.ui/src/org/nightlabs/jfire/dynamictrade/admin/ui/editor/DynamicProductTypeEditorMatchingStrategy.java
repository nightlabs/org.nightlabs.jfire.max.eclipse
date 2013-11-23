/**
 * 
 */
package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditorMatchingStrategy;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DynamicProductTypeEditorMatchingStrategy 
extends AbstractProductTypeAdminEditorMatchingStrategy 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditorMatchingStrategy#getProductTypeClass()
	 */
	@Override
	public Class<? extends ProductType> getProductTypeClass() {
		return DynamicProductType.class;
	}

}
