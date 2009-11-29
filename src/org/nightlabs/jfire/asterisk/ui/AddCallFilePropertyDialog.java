/**
 *
 */
package org.nightlabs.jfire.asterisk.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;

public class AddCallFilePropertyDialog
extends ResizableTrayDialog
{
	private Text keyText;
	private Text valueText;
	private String key;
	private String value;

	public AddCallFilePropertyDialog(Shell parentShell) {
		super(parentShell, Messages.RESOURCE_BUNDLE);
		// @Yo: Above, this was the wrong Messages class (due to the wrong import).
		// I ran the String externalisation and use the correct one, now. It must always be the one of the current project!
		// And btw. the package of the JFire-Base-Messages should not be exported anymore. Did you check-out already? I think
		// I removed the export in the MANIFEST.MF last week.
		// Btw. this resource bundle is for specifying the default size of the dialog. You can put properties for
		// the size (fully qualified class name of the dialog class + "height"/"width" - e.g. "org.nightlabs.my.MyDialog.width = 500")
		// and maybe the location, too (I usually specify the size, only).
		// Marco.

		//			setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.AddCallFilePropertyDialog.windowTitle")); //$NON-NLS-1$
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		// @Yo: Please read javadoc ;-) the preferred method is to use the super-method.
		//			XComposite mainComposite = new XComposite(parent, SWT.NONE);
		Composite mainComposite = (Composite) super.createDialogArea(parent);

		new Label(mainComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.AddCallFilePropertyDialog.keyLabel.text")); //$NON-NLS-1$

		keyText = new Text(mainComposite, SWT.BORDER);
		keyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		keyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(!keyText.getText().isEmpty());
			}
		});

		new Label(mainComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.AddCallFilePropertyDialog.valueLabel.text")); //$NON-NLS-1$
		valueText = new Text(mainComposite, SWT.BORDER);
		valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return mainComposite;
	}

	@Override
	protected void okPressed() {
		key = keyText.getText();
		value = valueText.getText();
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}