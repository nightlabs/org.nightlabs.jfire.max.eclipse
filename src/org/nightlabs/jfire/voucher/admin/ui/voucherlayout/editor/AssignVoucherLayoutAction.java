/**
 * 
 */
package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

class AssignVoucherLayoutAction extends SelectionAction {
	
	private RemoteVoucherLayoutComposite voucherLayoutComposite;
	private VoucherLayoutPage voucherLayoutPage;
	
	public AssignVoucherLayoutAction(VoucherLayoutPage page, RemoteVoucherLayoutComposite comp) {
		super();
		setId(AssignVoucherLayoutAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.AssignVoucherLayoutAction.text")); //$NON-NLS-1$
		this.voucherLayoutComposite = comp;
		this.voucherLayoutPage = page;
	}

	@Override
	public boolean calculateEnabled() {
		return voucherLayoutComposite.getSelectedLayout() != null;
	}

	@Override
	public boolean calculateVisible() {
		return true;
	}
	
	@Override
	public void run() {
		voucherLayoutPage.assignVoucherLayout(voucherLayoutComposite.getSelectedLayout());
	}
}