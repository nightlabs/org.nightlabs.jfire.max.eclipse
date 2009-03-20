/**
 * 
 */
package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherLayoutDAO;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.scripting.id.VoucherLayoutID;
import org.nightlabs.progress.NullProgressMonitor;

class StoreVoucherLayoutAction extends SelectionAction {
	
	private RemoteVoucherLayoutComposite voucherLayoutComposite;
	private VoucherLayoutPage voucherLayoutPage;
	
	public StoreVoucherLayoutAction(VoucherLayoutPage page, RemoteVoucherLayoutComposite comp) {
		super();
		setId(StoreVoucherLayoutAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.text")); //$NON-NLS-1$
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
		
		// fetch all file data for the voucher layout
		voucherLayout = VoucherLayoutDAO.sharedInstance().getVoucherLayout((VoucherLayoutID) JDOHelper.getObjectId(voucherLayout),
				new String[] {VoucherLayout.FETCH_GROUP_FILE}, 1, new NullProgressMonitor());
		
		String baseFolder = voucherLayoutPage.getLocalVoucherLayoutSection().getVoucherLayoutComposite().getBaseFolder();
		final File baseDirectory = new File(baseFolder);
		
		String error = null;
		InputDialog dlg = new InputDialog(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.inputDialog.title"), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.inputDialog.text"), voucherLayout.getFileName(), new IInputValidator() { //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			public String isValid(String newText) {
				if (new File(baseDirectory, newText).exists())
					return Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.inputDialog.fileNameExistsErrorMessage"); //$NON-NLS-1$
				else
					return null;
			}
		});
		
		do {
			dlg.setErrorMessage(error);
			if (dlg.open() != Window.OK)
				return;
			
			String filename = dlg.getValue();
			File file = new File(baseDirectory, filename);
			
			try {
				if (file.createNewFile()) {
					FileOutputStream fos = new FileOutputStream(file);
					try {
						fos.write(voucherLayout.getFileData());
					} finally {
						fos.close();
					}
					error = null;
				} else {
					MessageDialog.openError(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.fileNameExistsErrorDialog.title"), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.fileNameExistsErrorDialog.text")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} catch (IOException e) {
				error = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.StoreVoucherLayoutAction.enterValidFilenameMessage") + e.getMessage(); //$NON-NLS-1$
			}
		} while (error != null);
		
		voucherLayoutPage.getLocalVoucherLayoutSection().getVoucherLayoutComposite().loadLayouts(baseDirectory);
	}
}