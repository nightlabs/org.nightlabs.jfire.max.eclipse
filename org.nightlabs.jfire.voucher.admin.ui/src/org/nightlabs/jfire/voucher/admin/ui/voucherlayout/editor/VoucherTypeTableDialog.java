package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.ui.quicklist.VoucherTypeTable;

public class VoucherTypeTableDialog extends ResizableTrayDialog {
	
	private Collection<VoucherType> voucherTypes;
	private String title;
	private String topLabel;
	private String bottomLabel;

	public VoucherTypeTableDialog(Shell parentShell, Collection<VoucherType> voucherTypes, String title, String topLabel, String bottomLabel) {
		super(parentShell, Messages.RESOURCE_BUNDLE);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.voucherTypes = voucherTypes;
		
		this.title = title;
		this.topLabel = topLabel;
		this.bottomLabel = bottomLabel;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		XComposite twoColComp = new XComposite(area, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA, 2);
		GridLayout layout = twoColComp.getGridLayout();
		layout.horizontalSpacing = 15;
		
//		XComposite twoColComp = new XComposite(twoColComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
		Label label = new Label(twoColComp, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label.setImage(Display.getDefault().getSystemImage(SWT.ICON_QUESTION));
		GridData gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.verticalSpan = 3;
		label.setLayoutData(gd);
		
		label = new Label(twoColComp, SWT.WRAP);
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, label);
		label.setText(topLabel);
		
//		VoucherTypeTableComposite voucherTypeTable = new VoucherTypeTableComposite(twoColComp, SWT.NONE);
		
		VoucherTypeTable voucherTypeTable = new VoucherTypeTable(twoColComp, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
		
		voucherTypeTable.setInput(voucherTypes);
		
		label = new Label(twoColComp, SWT.WRAP);
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, label);
		label.setText(bottomLabel);
		
		return area;
	}
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}
}
