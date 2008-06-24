/**
 * 
 */
package org.nightlabs.jfire.voucher.admin.ui.editor;

import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditorMatchingStrategy;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class VoucherTypeEditorMatchingStrategy 
extends AbstractProductTypeAdminEditorMatchingStrategy 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditorMatchingStrategy#getProductTypeClass()
	 */
	@Override
	public Class<? extends ProductType> getProductTypeClass() {
		return VoucherType.class;
	}

}
