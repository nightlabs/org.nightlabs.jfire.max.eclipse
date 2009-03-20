/**
 * 
 */
package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.io.File;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;

class RenameVoucherLayoutAction extends SelectionAction {
	
	private LocalVoucherLayoutComposite voucherLayoutComposite;
	
	public RenameVoucherLayoutAction(LocalVoucherLayoutComposite comp) {
		super();
		setId(RenameVoucherLayoutAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.RenameVoucherLayoutAction.text")); //$NON-NLS-1$
		this.voucherLayoutComposite = comp;
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
		
		String filename = selectedVoucherLayout.getFileName();
		String baseFolder = voucherLayoutComposite.getBaseFolder();
		final File baseDirectory = new File(baseFolder);
		File file = new File(baseFolder, filename);

		String error = null;
		do {
			InputDialog dlg = new InputDialog(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.RenameVoucherLayoutAction.renameDialog.title"), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.RenameVoucherLayoutAction.renameDialog.text"), filename, new IInputValidator() { //$NON-NLS-1$ //$NON-NLS-2$
				@Override
				public String isValid(String newText) {
					if (new File(baseDirectory, newText).exists())
						return Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.RenameVoucherLayoutAction.filenameExistsErrorMessage"); //$NON-NLS-1$
					else
						return null;
				}
			});
			dlg.setErrorMessage(error);
			if (dlg.open() == Window.OK) {
				File newFile = new File(baseDirectory, dlg.getValue());
				if (file.renameTo(newFile))
					error = null;
				else
					error = Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.RenameVoucherLayoutAction.filenameInvalidErrorMessage"); //$NON-NLS-1$
			} else {
				return;
			}
		} while (error != null);
		
		voucherLayoutComposite.loadLayouts(baseDirectory);
	}
}