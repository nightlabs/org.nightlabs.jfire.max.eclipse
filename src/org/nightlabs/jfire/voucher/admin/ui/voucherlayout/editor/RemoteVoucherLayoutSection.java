package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;

public class RemoteVoucherLayoutSection extends ToolBarSectionPart {

	private RemoteVoucherLayoutComposite voucherLayoutComposite;

	public RemoteVoucherLayoutSection(VoucherLayoutPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.RemoteVoucherLayoutSection.title")); //$NON-NLS-1$
		
		this.voucherLayoutComposite = new RemoteVoucherLayoutComposite(getContainer(), SWT.NONE, true);
		this.voucherLayoutComposite.getLayoutTable().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				updateToolBarManager();
			}
		});
		
		AssignVoucherLayoutAction assignAction = new AssignVoucherLayoutAction(page, voucherLayoutComposite);
		registerAction(assignAction, true);

		DeleteVoucherLayoutAction deleteAction = new DeleteVoucherLayoutAction(page, voucherLayoutComposite);
		registerAction(deleteAction, true);

		StoreVoucherLayoutAction storeAction = new StoreVoucherLayoutAction(page, voucherLayoutComposite);
		registerAction(storeAction, true);
		
		updateToolBarManager();
	}
	
	public RemoteVoucherLayoutComposite getVoucherLayoutComposite() {
		return voucherLayoutComposite;
	}

	public void selectVoucherLayout(VoucherLayout VoucherLayout) {
		voucherLayoutComposite.selectLayout(VoucherLayout);
	}
}
