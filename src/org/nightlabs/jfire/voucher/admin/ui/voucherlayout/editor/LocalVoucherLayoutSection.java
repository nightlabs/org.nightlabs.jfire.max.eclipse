package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

public class LocalVoucherLayoutSection extends ToolBarSectionPart {

	private LocalVoucherLayoutComposite voucherLayoutComposite;

	public LocalVoucherLayoutSection(VoucherLayoutPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.LocalVoucherLayoutSection.title")); //$NON-NLS-1$
		
		voucherLayoutComposite = new LocalVoucherLayoutComposite(getContainer(), SWT.NONE);
		voucherLayoutComposite.getLayoutTable().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				updateToolBarManager();
			}
		});
		
		UploadVoucherLayoutAction uploadAction = new UploadVoucherLayoutAction(page, voucherLayoutComposite);
		registerAction(uploadAction, true);

		RenameVoucherLayoutAction renameAction = new RenameVoucherLayoutAction(voucherLayoutComposite);
		registerAction(renameAction, true);

		updateToolBarManager();
		
		setSelectionProvider(voucherLayoutComposite.getLayoutTable());
	}
	
	public LocalVoucherLayoutComposite getVoucherLayoutComposite() {
		return voucherLayoutComposite;
	}
}
