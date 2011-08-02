package org.nightlabs.jfire.auth.ui.ldap.tree;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;

/**
 * Dialog for entering login and password for binding against LDAP directory.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPBindCredentialsDialog extends ResizableTitleAreaDialog{
	
	private String login;
	private String password;
	private Text loginText;
	private Text passwordText;

	/**
	 * Contructs new {@link LDAPBindCredentialsDialog}.
	 * 
	 * @param shell Parent {@link Shell}
	 * @param resourceBundle The resource bundle to use for initial size and location hints. May be <code>null</code>.
	 */
	public LDAPBindCredentialsDialog(Shell shell, ResourceBundle resourceBundle) {
		super(shell, resourceBundle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wrapper = (Composite) super.createDialogArea(parent);
		wrapper.setLayout(new GridLayout(1, false));
		wrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

		setTitle(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.tree.LDAPBindCredentialsDialog.dlgTitle")); //$NON-NLS-1$
		setMessage(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.tree.LDAPBindCredentialsDialog.dlgMessage")); //$NON-NLS-1$
		setTitleImage(
				SharedImages.getSharedImage(LdapUIPlugin.sharedInstance(), LDAPBindCredentialsDialog.class, "titleImage", "66x75", ImageFormat.png)); //$NON-NLS-1$ //$NON-NLS-2$
		
		Composite dialogAreaParent = new Composite(wrapper, SWT.NONE);
		dialogAreaParent.setLayout(new GridLayout(2, false));
		dialogAreaParent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		new Label(dialogAreaParent, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.tree.LDAPBindCredentialsDialog.loginLabel")); //$NON-NLS-1$
		loginText = new Text(dialogAreaParent, SWT.BORDER);
		loginText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(dialogAreaParent, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.tree.LDAPBindCredentialsDialog.passwordLabel")); //$NON-NLS-1$
		passwordText = new Text(dialogAreaParent, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return wrapper;
	}
	
	@Override
	protected void okPressed() {
		login = loginText.getText();
		password = passwordText.getText();
		super.okPressed();
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}

}
