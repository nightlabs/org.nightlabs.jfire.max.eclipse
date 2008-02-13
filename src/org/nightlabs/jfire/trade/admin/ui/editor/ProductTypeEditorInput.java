package org.nightlabs.jfire.trade.admin.ui.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeEditorInput
extends JDOObjectEditorInput<ProductTypeID>
{
	/**
	 * @param jdoObjectID
	 */
	public ProductTypeEditorInput(ProductTypeID productTypeID) {
		super(productTypeID);
	}

}
