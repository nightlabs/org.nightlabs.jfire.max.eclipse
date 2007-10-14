package org.nightlabs.jfire.dynamictrade.admin.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypePriceConfigPageController;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class DynamicProductTypePriceConfigPageController 
extends ProductTypePriceConfigPageController 
{
	/**
	 * @param editor
	 */
	public DynamicProductTypePriceConfigPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public DynamicProductTypePriceConfigPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}
}
