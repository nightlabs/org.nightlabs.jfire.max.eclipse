package org.nightlabs.jfire.auth.ui.ldap.tree;

import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.jfire.base.security.integration.ldap.connection.LDAPConnection;

/**
 * Dialog with a {@link LDAPTree} representing LDAP entries structure of a LDAP directory which is reachable
 * by provided connection parameters.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class SelectLDAPEntryDialog extends ResizableTitleAreaDialog{

	private LDAPTree ldapTree;
	private Set<LDAPTreeEntry> selectedElements;
	
	/**
	 * Parameters for {@link LDAPConnection} which are passed to {@link LDAPTree}.
	 */
	private ILDAPConnectionParamsProvider ldapConnectionParamsProvider;

	/**
	 * Constructs a new {@link SelectLDAPEntryDialog}. Note that {@link ILDAPConnectionParamsProvider} is a MUST parameter although
	 * you could specify it later with {@link #setLdapConnectionParamsProvider(ILDAPConnectionParamsProvider)} BEFORE opening this dialog.
	 * 
	 * @param shell Parent {@link Shell}
	 * @param resourceBundle The resource bundle to use for initial size and location hints. May be <code>null</code>.
	 * @param ldapConnectionParamsProvider {@link LDAPConnection} parameters provider
	 */
	public SelectLDAPEntryDialog(Shell shell, ResourceBundle resourceBundle, ILDAPConnectionParamsProvider ldapConnectionParamsProvider) {
		super(shell, resourceBundle);
		this.ldapConnectionParamsProvider = ldapConnectionParamsProvider;
	}
	
	/**
	 * Set {@link ILDAPConnectionParamsProvider} to be passed to {@link LDAPTree}. Can't be <code>null</code>.
	 * 
	 * @param ldapConnectionParamsProvider can't be <code>null</code> or {@link IllegalArgumentException} will be thrown otherwise
	 */
	public void setLdapConnectionParamsProvider(ILDAPConnectionParamsProvider ldapConnectionParamsProvider) {
		if (ldapConnectionParamsProvider == null){
			throw new IllegalArgumentException("ILDAPConnectionParamsProvider can't be null!");
		}
		this.ldapConnectionParamsProvider = ldapConnectionParamsProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wrapper = (Composite) super.createDialogArea(parent);
		wrapper.setLayout(new GridLayout(1, false));
		wrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

		setTitle("Select LDAP entry");
		setMessage("Please select at least one LDAP entry");
		setTitleImage(
				SharedImages.getSharedImage(LdapUIPlugin.sharedInstance(), SelectLDAPEntryDialog.class, "titleImage", "66x75", ImageFormat.png));


		ldapTree = new LDAPTree(wrapper, SWT.BORDER);
		ldapTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		ldapTree.setInput(ldapConnectionParamsProvider);
		ldapTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Button okButton = getButton(OK);
				if (okButton != null && !okButton.isDisposed()){
					okButton.setEnabled(event.getSelection() != null && !event.getSelection().isEmpty());
				}
			}
		});

		return wrapper;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		selectedElements = ldapTree.getSelectedElements();
		super.okPressed();
	}
	
	/**
	 * Get selected {@link LDAPTree} elements.
	 * 
	 * @return {@link Set} of selected {@link LDAPTreeEntry} objects
	 */
	public Set<LDAPTreeEntry> getSelectedElements() {
		return selectedElements;
	}
}
