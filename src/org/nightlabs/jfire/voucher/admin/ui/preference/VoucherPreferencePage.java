package org.nightlabs.jfire.voucher.admin.ui.preference;

import org.nightlabs.base.ui.preference.CategoryPreferencePage;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

public class VoucherPreferencePage extends CategoryPreferencePage {

	/**
	 *
	 */
	public VoucherPreferencePage() {
	}

	@Override
	protected String getText() {
		return Messages.getString("org.nightlabs.jfire.voucher.admin.ui.preference.VoucherPreferencePage.text"); //$NON-NLS-1$
	}
}