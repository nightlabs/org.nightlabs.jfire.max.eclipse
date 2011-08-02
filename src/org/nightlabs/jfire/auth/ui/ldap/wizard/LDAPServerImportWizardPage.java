package org.nightlabs.jfire.auth.ui.ldap.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.statushandlers.StatusManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTree;
import org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTreeEntry;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.util.CollectionUtil;

/**
 * Wizard page contributed to {@link ImportExportWizard} by {@link LDAPServerImportExportWizardHop}.
 * Used when {@link SyncDirection} was IMPORT. Import options could be selected here: import all or 
 * import selected LDAP entries only. LDAP entries are selected with the help of {@link LDAPTree}.
 *  
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerImportWizardPage extends WizardHopPage{
	
	private ILDAPConnectionParamsProvider treeConnectionParamsProvider;
	private LDAPTree ldapTree;
	
	private Set<LDAPTreeEntry> selectedLDAPEntries;
	private boolean shouldImportAll = true;
	
	private Button importAllButton;
	private Button importSelectedButton;
	
	private boolean canFinish = false;
	
	/**
	 * Default constructor
	 */
	public LDAPServerImportWizardPage() {
		super(LDAPServerImportWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.pageTitle"), SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerImportWizardPage.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.pageDescription")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		
		importAllButton = new Button(parent, SWT.RADIO);
		importAllButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.importAllButtonLabel")); //$NON-NLS-1$
		importAllButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.importAllButtonTooltip")); //$NON-NLS-1$
		importAllButton.setSelection(true);
		importAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (importAllButton.getSelection() 
						&& ldapTree != null && ldapTree.isVisible()){
					shouldImportAll = true;
					ldapTree.setEnabled(false);
					updateStatus(null);
				}
			}
		});
		
		importSelectedButton = new Button(parent, SWT.RADIO);
		importSelectedButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.importSelectedButtonLabel")); //$NON-NLS-1$
		importSelectedButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.importSelectedButtonTooltip")); //$NON-NLS-1$
		importSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ldapTree != null && importSelectedButton.getSelection()){
					shouldImportAll = false;
					ldapTree.setInput(treeConnectionParamsProvider);
					ldapTree.setEnabled(true);
					updateStatus(null);
				}
			}
		});
		
		ldapTree = new LDAPTree(parent, SWT.BORDER | SWT.MULTI);
		ldapTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		ldapTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (importSelectedButton.getSelection()){
					canFinish = true;
					updateStatus(null);
					selectedLDAPEntries = ldapTree.getSelectedElements();
					if (selectedLDAPEntries == null || selectedLDAPEntries.isEmpty()){
						updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.pageStatus_noEntriesSelected")); //$NON-NLS-1$
					}else{
						for (LDAPTreeEntry treeEntry : selectedLDAPEntries){
							if (treeEntry.hasAttributesLoaded()){
								try{
									if (!treeEntry.getAttributes(null).containsAnyAttributeValue("objectClass", CollectionUtil.createHashSet("person", "posixAccount"))){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
										canFinish = false;
										updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportWizardPage.pageStatus_entryShouldHaveAttributes")); //$NON-NLS-1$
										break;
									}
								} catch (Exception e) {
									// just writing to log
									StatusManager.getManager().handle(
											new Status(Status.ERROR, LdapUIPlugin.PLUGIN_ID, e.getMessage(), e),
											StatusManager.LOG
											);
								}
							}
						}
					}
				}
			}
		});
		ldapTree.setEnabled(false);
		
		setControl(parent);
		return parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBeLastPage() {
		if (shouldImportAll){
			return true;
		}
		return canFinish;
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public boolean isPageComplete() {
		if (importAllButton != null && !importAllButton.isDisposed() 
				&& importAllButton.getSelection()){
			return true;
		}else if (importSelectedButton != null && !importSelectedButton.isDisposed()
				&& importSelectedButton.getSelection() 
				&& ldapTree != null && !ldapTree.isDisposed()
				&& ldapTree.getSelection() != null && !ldapTree.getSelection().isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * Set {@link ILDAPConnectionParamsProvider} to be used in {@link LDAPTree}.
	 * 
	 * @param ldapConnectionParamsProvider
	 */
	public void setLdapConnectionParamsProvider(ILDAPConnectionParamsProvider ldapConnectionParamsProvider) {
		this.treeConnectionParamsProvider = ldapConnectionParamsProvider;
	}
	
	/**
	 * Check whether all LDAP entries should be imported into JFire
	 * 
	 * @return <code>true</code> if everything should be imported
	 */
	public boolean shouldImportAll(){
		return shouldImportAll;
	}
	
	/**
	 * Get selected LDAP entries names to be imported
	 * 
	 * @return
	 */
	public Collection<String> getSelectedEntries(){
		if (selectedLDAPEntries == null){
			return new ArrayList<String>();
		}
		Collection<String> entriesForImport = new ArrayList<String>(selectedLDAPEntries.size());
		for (LDAPTreeEntry entry : selectedLDAPEntries){
			entriesForImport.add(entry.getName());
		}
		return entriesForImport;
	}
	
}
