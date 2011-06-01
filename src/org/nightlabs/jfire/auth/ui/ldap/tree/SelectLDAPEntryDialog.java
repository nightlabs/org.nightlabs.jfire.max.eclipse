package org.nightlabs.jfire.auth.ui.ldap.tree;

import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.statushandlers.StatusManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttribute;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
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
	 * If this field is set than this {@link LDAPAttributeSet} is used as a selection criteria inside {@link LDAPTree}.
	 * It means that entries which have the same attrioute values will be allowed for selection.
	 */
	private LDAPAttributeSet selectionCriteriaAttributes;
	
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
	 * Set selection criteria. See {@link #selectionCriteriaAttributes} description.
	 * 
	 * @param selectionCriteriaAttributes {@link LDAPAttributeSet} used as selection criteria
	 */
	public void setSelectionCriteriaAttributes(LDAPAttributeSet selectionCriteriaAttributes) {
		this.selectionCriteriaAttributes = selectionCriteriaAttributes;
	}

	/**
	 * Get selected {@link LDAPTree} elements.
	 * 
	 * @return {@link Set} of selected {@link LDAPTreeEntry} objects
	 */
	public Set<LDAPTreeEntry> getSelectedElements() {
		return selectedElements;
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
					okButton.setEnabled(event.getSelection() != null && !event.getSelection().isEmpty() && isSelectionAllowed());
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
	
	private boolean isSelectionAllowed(){
		if (selectionCriteriaAttributes == null
				|| ldapTree.getInput() instanceof ILDAPConnectionParamsProvider){
			setErrorMessage(null);
			return true;
		}
		
		Set<LDAPTreeEntry> selectedElements = ldapTree.getSelectedElements();
		for (LDAPTreeEntry ldapTreeEntry : selectedElements) {
			if (ldapTreeEntry.hasAttributesLoaded()){
				try {
					LDAPAttributeSet attributes = ldapTreeEntry.getAttributes(null);
					for (LDAPAttribute<Object> ldapAttribute : selectionCriteriaAttributes) {
						if (!attributes.containsAllAttributeValues(ldapAttribute.getName(), ldapAttribute.getValues())){
							setErrorMessage("Selected entry should contain these attributes: " + selectionCriteriaAttributes.toString());
							return false;
						}
					}
				} catch (Exception e) {
					// just writing to log and returning true
					StatusManager.getManager().handle(
							new Status(Status.ERROR, LdapUIPlugin.PLUGIN_ID, e.getMessage(), e),
							StatusManager.LOG
							);
				}
			}
		}
		setErrorMessage(null);
		return true;
	}
}
