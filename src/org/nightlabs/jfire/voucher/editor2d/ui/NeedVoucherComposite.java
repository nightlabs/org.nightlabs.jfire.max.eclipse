package org.nightlabs.jfire.voucher.editor2d.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.voucher.editor2d.ui.dialog.VoucherChooseDialog;
import org.nightlabs.jfire.voucher.editor2d.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class NeedVoucherComposite 
extends XComposite 
{
	public NeedVoucherComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	public NeedVoucherComposite(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);		
	}

	protected void createComposite(Composite parent) 
	{
		setLayoutData(new GridData(GridData.FILL_BOTH));;
		
		Label label = new Label(this, SWT.WRAP);
		label.setText(Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.NeedVoucherComposite.label.needVoucher")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button needVoucherButton = new Button(this, SWT.PUSH);
		needVoucherButton.setText(Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.NeedVoucherComposite.button.selectVoucher")); //$NON-NLS-1$
		needVoucherButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				Display.getDefault().asyncExec(new Runnable() 
				{		
					public void run() {
						VoucherChooseDialog voucherChooseDialog = new VoucherChooseDialog(getShell());
						voucherChooseDialog.open();
					}
				});			
			}
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
	}
}
