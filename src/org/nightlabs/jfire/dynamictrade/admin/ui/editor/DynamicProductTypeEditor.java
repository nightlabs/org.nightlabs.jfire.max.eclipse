package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeEditor
extends AbstractProductTypeAdminEditor
{
	public static final String EDITOR_ID = DynamicProductTypeEditor.class.getName();
	
	public DynamicProductTypeEditor() {
		super();
		setShowOverviewPage(true);
	}
}
