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
		super(LDAPServerImportWizardPage.class.getName(), "Import from LDAP server", SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerImportWizardPage.class));
		setDescription("Run import all entries from LDAP server or select entries to be imported");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		
		importAllButton = new Button(parent, SWT.RADIO);
		importAllButton.setText("Import all");
		importAllButton.setToolTipText("Import all eligible LDAP entries (their parent(s) are specified in LDAP scripts)");
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
		importSelectedButton.setText("Import selected only");
		importSelectedButton.setToolTipText("Import selected LDAP user related entries only");
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
						updateStatus("Select at least one entry!");
					}else{
						for (LDAPTreeEntry treeEntry : selectedLDAPEntries){
							if (treeEntry.hasAttributesLoaded()){
								try{
									if (!treeEntry.getAttributes(null).containsAnyAttributeValue("objectClass", CollectionUtil.createHashSet("person", "posixAccount"))){
										canFinish = false;
										updateStatus("Selected entry should have these attributes: objectClass=person or posixAccount");
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
