package org.nightlabs.jfire.dynamictrade.admin.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractDynamicProductTypePageController 
 extends AbstractProductTypePageController
{
	/**
	 * @param editor
	 */
	public AbstractDynamicProductTypePageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public AbstractDynamicProductTypePageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	public DynamicProductType getDynamicProductType() {
		return (DynamicProductType) getProductType();
	}
	
}
