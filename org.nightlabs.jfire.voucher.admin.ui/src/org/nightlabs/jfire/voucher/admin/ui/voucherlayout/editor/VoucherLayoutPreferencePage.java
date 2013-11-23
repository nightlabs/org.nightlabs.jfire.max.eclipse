package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.io.IOException;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

public class VoucherLayoutPreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{
	
	public VoucherLayoutPreferencePage() {
		super(GRID);
		setPreferenceStore(VoucherAdminPlugin.getDefault().getPreferenceStore());
		setTitle(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.VoucherLayoutPreferencePage.title")); //$NON-NLS-1$
	}
	
	@Override
	protected void createFieldEditors() {
		DirectoryFieldEditor directoryFieldEditor = new DirectoryFieldEditor(VoucherLayoutPreferences.LOCAL_VOUCHER_LAYOUT_BASE_DIRECTORY, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.VoucherLayoutPreferencePage.localVoucherLayoutBaseDirectoryField.description"), getFieldEditorParent()); //$NON-NLS-1$
		addField(directoryFieldEditor);
	}
	
	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		
		IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();
		try {
			store.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return ok;
	}

	@Override
	public void init(IWorkbench arg0) {
	}
}
