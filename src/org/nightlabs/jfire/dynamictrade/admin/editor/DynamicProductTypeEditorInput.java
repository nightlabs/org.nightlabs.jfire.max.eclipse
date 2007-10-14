package org.nightlabs.jfire.dynamictrade.admin.editor;

import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;

public class DynamicProductTypeEditorInput
//extends JDOObjectEditorInput<ProductTypeID>
extends ProductTypeEditorInput
{
	public DynamicProductTypeEditorInput(ProductTypeID dynamicProductTypeID) {
		super(dynamicProductTypeID);
	}
}
