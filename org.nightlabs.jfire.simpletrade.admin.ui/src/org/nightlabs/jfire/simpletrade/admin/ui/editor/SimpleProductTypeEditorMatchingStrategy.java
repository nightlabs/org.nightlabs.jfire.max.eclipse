/**
 * 
 */
package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditorMatchingStrategy;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class SimpleProductTypeEditorMatchingStrategy 
extends AbstractProductTypeAdminEditorMatchingStrategy 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditorMatchingStrategy#getProductTypeClass()
	 */
	@Override
	public Class<? extends ProductType> getProductTypeClass() {
		return SimpleProductType.class;
	}
}
 