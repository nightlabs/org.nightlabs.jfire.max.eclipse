package org.nightlabs.jfire.voucher.admin.ui.editor;

import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeEditor
	extends AbstractProductTypeAdminEditor
{
	public static final String EDITOR_ID = VoucherTypeEditor.class.getName();
	
	public VoucherTypeEditor() {
		super();
		setShowOverviewPage(true);
	}

}
