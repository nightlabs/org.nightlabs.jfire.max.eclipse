package org.nightlabs.jfire.voucher.admin.editor;

import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeEditorInput 
//extends JDOObjectEditorInput<ProductTypeID> 
extends ProductTypeEditorInput
{
	public VoucherTypeEditorInput(ProductTypeID eventID) {
		super(eventID);
	}
}
