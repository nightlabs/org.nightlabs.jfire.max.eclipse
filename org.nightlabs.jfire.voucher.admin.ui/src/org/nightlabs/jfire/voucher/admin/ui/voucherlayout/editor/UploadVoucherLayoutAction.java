/**
 * 
 */
package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherLayoutDAO;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.scripting.id.VoucherLayoutID;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.ui.quicklist.VoucherTypeQuickListFilter;

class UploadVoucherLayoutAction extends SelectionAction {
	
	private LocalVoucherLayoutComposite voucherLayoutComposite;
	private VoucherLayoutPage voucherLayoutPage;
	
	public UploadVoucherLayoutAction(VoucherLayoutPage page, LocalVoucherLayoutComposite comp) {
		super();
		setId(UploadVoucherLayoutAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.text")); //$NON-NLS-1$
		this.voucherLayoutComposite = comp;
		this.voucherLayoutPage = page;
	}

	@Override
	public boolean calculateEnabled() {
		return !getSelection().isEmpty();
//		return voucherLayoutComposite.getSelectedVoucherLayout() != null;
	}

	@Override
	public boolean calculateVisible() {
		return true;
	}
	
	@Override
	public void run() {
		VoucherLayout selectedVoucherLayout = voucherLayoutComposite.getSelectedLayout();
		
//		voucherLayoutPage.switchToProgress();
		IProgressMonitor monitor = voucherLayoutPage.getProgressMonitor();
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.uploadTask.name"), 10); //$NON-NLS-1$
		try {
			Collection<VoucherLayoutID> voucherLayoutIds = VoucherLayoutDAO.sharedInstance().getVoucherLayoutIdsByFileName(selectedVoucherLayout.getFileName());
			if (!voucherLayoutIds.isEmpty()) {
				Collection<VoucherType> affectedVoucherTypes = new HashSet<VoucherType>();
				for (VoucherLayoutID voucherLayoutId : voucherLayoutIds) {
					Collection<VoucherType> voucherTypes = VoucherTypeDAO.sharedInstance().getVoucherTypesByVoucherLayoutId(voucherLayoutId, VoucherTypeQuickListFilter.FETCH_GROUPS_VOUCHER_TYPE,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
					
					monitor.worked(2);
					
					affectedVoucherTypes.addAll(voucherTypes);
				}
				
				boolean overwrite = false;
				String title = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.overwriteDialog.title"); //$NON-NLS-1$
				String message = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.overwriteDialog.message1"); //$NON-NLS-1$
				String message2 = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.overwriteDialog.message2"); //$NON-NLS-1$
				String message3 = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.overwriteDialog.message3"); //$NON-NLS-1$
				if (affectedVoucherTypes.isEmpty()) {
					message += Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.overwriteDialog.message4"); //$NON-NLS-1$
					String text = message + "\n\n" + message2 + "\n\n" + message3; //$NON-NLS-1$ //$NON-NLS-2$
					if (MessageDialog.openConfirm(RCPUtil.getActiveShell(), title, text))
						overwrite = true;
				} else {
					message += Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.UploadVoucherLayoutAction.overwriteDialog.message5"); //$NON-NLS-1$
					
					VoucherTypeTableDialog dlg = new VoucherTypeTableDialog(RCPUtil.getActiveShell(), affectedVoucherTypes, title, message, message2 + "\n\n" + message3); //$NON-NLS-1$
					if (dlg.open() == Window.OK)
						overwrite = true;
				}
				
				if (overwrite) {
					VoucherLayout newLayout = new VoucherLayout(IDGenerator.getOrganisationID(), IDGenerator.nextID(VoucherLayout.class));
					newLayout.copyValuesFrom(selectedVoucherLayout);
					
					for (VoucherLayoutID voucherLayoutId : voucherLayoutIds) {
						VoucherLayoutDAO.sharedInstance().replaceVoucherLayout(voucherLayoutId, newLayout);
					}
				}
			} else {
				// No voucher layout with the same name exists so just store it
				VoucherLayout newLayout = new VoucherLayout(IDGenerator.getOrganisationID(), IDGenerator.nextID(VoucherLayout.class));
				newLayout.copyValuesFrom(selectedVoucherLayout);
				VoucherLayoutDAO.sharedInstance().storeJDOObject(newLayout, false, null, -1, null);
			}
		} finally {
			monitor.done();
//			voucherLayoutPage.switchToContent();
		}
	}
}