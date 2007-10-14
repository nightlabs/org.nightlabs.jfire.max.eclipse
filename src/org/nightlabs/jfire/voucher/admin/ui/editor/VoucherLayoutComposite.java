package org.nightlabs.jfire.voucher.admin.ui.editor;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherLayoutComposite 
extends XComposite 
{

	public VoucherLayoutComposite(Composite parent, int style, VoucherType voucherType, 
			IDirtyStateManager dirtyStateManager) 
	{
		super(parent, style);		
		this.voucherType = voucherType;
		this.dirtyStateManager = dirtyStateManager;
		createComposite(this);
	}

	private IDirtyStateManager dirtyStateManager;
	private Text voucherLayoutText = null; 
	private Button browseButton = null;
	protected void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(2, false));
		voucherLayoutText = new Text(parent, SWT.BORDER);
		voucherLayoutText.setEditable(false);
		voucherLayoutText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browseButton = new Button(parent, SWT.NONE);
		browseButton.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.VoucherLayoutComposite.browseButton.text")); //$NON-NLS-1$
		browseButton.addSelectionListener(buttonListener);

		setVoucherType(voucherType);
	}
		
	private VoucherType voucherType = null;
	protected VoucherType getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(VoucherType voucherType) 
	{
		this.voucherType = voucherType;		
		if (voucherType != null) {
			if (voucherType.getVoucherLayout() != null) {
				voucherLayoutText.setText(voucherType.getVoucherLayout().getFileName());
			} else {
				voucherLayoutText.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.VoucherLayoutComposite.voucherLayoutText.text_noVoucherLayoutAssigned")); //$NON-NLS-1$
			}			
		}
		else {
			voucherLayoutText.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.VoucherLayoutComposite.voucherLayoutText.text_noVoucherType")); //$NON-NLS-1$
		}
	}
	
	private File selectedFile = null;
	public File getSelectedFile() {
		return selectedFile;
	}
	
	private SelectionListener buttonListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			FileDialog fileDialog = new FileDialog(getShell());
			fileDialog.setFilterExtensions(new String[] {"*.v2d"}); //$NON-NLS-1$
			String fileName = fileDialog.open();
			if (fileName != null) {
				selectedFile = new File(fileName);
				voucherLayoutText.setText(selectedFile.getName());
				
				if (dirtyStateManager != null)
					dirtyStateManager.markDirty();
			}
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};
}
