package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherLayoutDAO;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.scripting.id.VoucherLayoutID;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.ui.quicklist.VoucherTypeQuickListFilter;
import org.nightlabs.progress.NullProgressMonitor;

class DeleteVoucherLayoutAction extends SelectionAction {
	
	private RemoteVoucherLayoutComposite voucherLayoutComposite;
	private VoucherLayoutPage voucherLayoutPage;
	
	public DeleteVoucherLayoutAction(VoucherLayoutPage page, RemoteVoucherLayoutComposite comp) {
		super();
		setId(DeleteVoucherLayoutAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.DeleteVoucherLayoutAction.text")); //$NON-NLS-1$
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
		VoucherLayout voucherLayout = voucherLayoutComposite.getSelectedLayout();
		
		if (voucherLayout.equals(voucherLayoutPage.getAssignedVoucherLayout())) {
			MessageDialog.openError(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.DeleteVoucherLayoutAction.cannotDeleteAssignedLayoutErrorDialog.title"), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.DeleteVoucherLayoutAction.cannotDeleteAssignedLayoutErrorDialog.text")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		Collection<VoucherType> vouchers = VoucherTypeDAO.sharedInstance().getVoucherTypesByVoucherLayoutId((VoucherLayoutID) JDOHelper.getObjectId(voucherLayout),
				VoucherTypeQuickListFilter.FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		
		
		
		if (vouchers.isEmpty()) {
			VoucherLayoutDAO.sharedInstance().deleteVoucherLayout(voucherLayout);
		} else {
			String title = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.DeleteVoucherLayoutAction.deleteErrorDialog.title"); //$NON-NLS-1$
			String message1 = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.DeleteVoucherLayoutAction.deleteErrorDialog.message1"); //$NON-NLS-1$
			String message2 = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.DeleteVoucherLayoutAction.deleteErrorDialog.message2"); //$NON-NLS-1$
			VoucherTypeTableDialog dlg = new VoucherTypeTableDialog(RCPUtil.getActiveShell(), vouchers, title, message1, message2) {
				@Override
				protected void createButtonsForButtonBar(Composite parent) {
					createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
				}
			};
			
			dlg.open();
		}
		
	}
}


