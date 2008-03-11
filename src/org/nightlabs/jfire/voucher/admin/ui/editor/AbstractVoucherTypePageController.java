package org.nightlabs.jfire.voucher.admin.ui.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 * @deprecated I think this class is not used at all. Delete it? Marco.
 */
public abstract class AbstractVoucherTypePageController
extends AbstractProductTypePageController
{
	/**
	 * @param editor
	 */
	public AbstractVoucherTypePageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public AbstractVoucherTypePageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}
	
	public VoucherType getVoucherType() {
		return (VoucherType) getProductType();
	}
}
