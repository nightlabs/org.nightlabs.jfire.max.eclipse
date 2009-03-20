package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeDetailPageController;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;

import com.ibm.icu.text.DateFormat;

public class AssignedVoucherLayoutSection extends MessageSectionPart {

	private Text voucherLayoutNameText;
	private VoucherLayoutPage voucherLayoutPage;
	private VoucherLayout assignedVoucherLayout;

	public AssignedVoucherLayoutSection(VoucherLayoutPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.AssignedVoucherLayoutSection.title")); //$NON-NLS-1$
		
		XComposite comp = new XComposite(getContainer(), SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
		new Label(comp, SWT.WRAP).setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.AssignedVoucherLayoutSection.assignedVoucherLayoutLabel.text")); //$NON-NLS-1$
		voucherLayoutNameText = new Text(comp, SWT.READ_ONLY | comp.getBorderStyle());
		voucherLayoutNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.voucherLayoutPage = page;
	}
	
	public void setVoucherLayout(VoucherLayout voucherLayout) {
		if (voucherLayout == null)
			voucherLayoutNameText.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.AssignedVoucherLayoutSection.noVoucherLayoutAssignedPlaceholder")); //$NON-NLS-1$
		else
			voucherLayoutNameText.setText(voucherLayout.getFileName() + " (" + DateFormat.getDateTimeInstance().format(voucherLayout.getFileTimestamp()) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.assignedVoucherLayout = voucherLayout;
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		
		((VoucherTypeDetailPageController) voucherLayoutPage.getPageController()).setVoucherLayout(assignedVoucherLayout);
	}
}
