package org.nightlabs.jfire.auth.ui.ldap.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.auth.ui.ldap.LDAPEntrySelectorComposite;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizard;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
import org.nightlabs.jfire.base.security.integration.ldap.sync.AttributeStructFieldSyncHelper;
import org.nightlabs.jfire.base.security.integration.ldap.sync.AttributeStructFieldSyncHelper.LDAPAttributeSyncPolicy;
import org.nightlabs.util.CollectionUtil;

/**
 * Wizard page contributed to {@link CreateUserManagementSystemWizard} by {@link CreateLDAPServerWizardHop}.
 * Used for configuring advanced {@link LDAPServer} properties: leading state, JFire-LDAP synchronization, base entry. 
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerAdvancedConfigWizardPage extends WizardHopPage{
	
	private Button isLeadingButton;
	private LDAPEntrySelectorComposite syncDNselector;
	private Text syncPasswordText;
	private LDAPEntrySelectorComposite ldapEntrySelector;
	private CCombo attributeSyncPolicyCombo;

	/**
	 * Default constructor
	 */
	public LDAPServerAdvancedConfigWizardPage() {
		super(LDAPServerAdvancedConfigWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.pageTitle"), SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerAdvancedConfigWizardPage.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.pageDescription")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		
		new Label(parent, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.baseEntryLabel")); //$NON-NLS-1$
		LDAPAttributeSet selectionCriteria = new LDAPAttributeSet();
		selectionCriteria.createAttribute("objectClass", CollectionUtil.createHashSet("organizationalUnit")); //$NON-NLS-1$ //$NON-NLS-2$
		ldapEntrySelector = new LDAPEntrySelectorComposite(parent, SWT.NONE, selectionCriteria);
		ldapEntrySelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ldapEntrySelector.setLdapConnectionParamsProvider(((CreateLDAPServerWizardHop) getWizardHop()).getLDAPConnectionParamsProvider());
		ldapEntrySelector.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyevent) {
				String selectedEntryName = ldapEntrySelector.getEntryName();
				if (selectedEntryName == null || selectedEntryName.isEmpty()){
					updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.pageStatus_noBaseEntry")); //$NON-NLS-1$
				}else{
					updateStatus(null);
				}
			}
		});
		
		ExpandableComposite baseEntryDescriptionExpandable = createDescriptionExpandable(
				parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.baseEntryDescription")); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.widthHint = 500;
		baseEntryDescriptionExpandable.setLayoutData(gd);

		
		Label separatorLdabel = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separatorLdabel.setLayoutData(gd);
		
		
		isLeadingButton = new Button(parent, SWT.CHECK);
		isLeadingButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.isLeadingCheckButonLabel")); //$NON-NLS-1$
		isLeadingButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.isLeadingCheckButtonTooltip")); //$NON-NLS-1$
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.verticalIndent = 10;
		isLeadingButton.setLayoutData(gd);
		
		ExpandableComposite leadingSystemDescriptionExpandable = createDescriptionExpandable(
				parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.leadingSystemDescription")); //$NON-NLS-1$
		gd = new GridData();
		gd.widthHint = 500;
		gd.verticalIndent = 10;
		leadingSystemDescriptionExpandable.setLayoutData(gd);
		
		
		Label attSyncLabel = new Label(parent, SWT.NONE);
		attSyncLabel.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.attributeSyncPolicyLabel")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		attSyncLabel.setLayoutData(gd);
		
		attributeSyncPolicyCombo = new CCombo(parent, SWT.BORDER);
		attributeSyncPolicyCombo.setEditable(false);
		attributeSyncPolicyCombo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		attributeSyncPolicyCombo.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.attributeSyncPolicyComboTooltip")); //$NON-NLS-1$
		attributeSyncPolicyCombo.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		attributeSyncPolicyCombo.setItems(LDAPAttributeSyncPolicy.getPossibleAttributeSyncPolicyValues());
		attributeSyncPolicyCombo.setText(LDAPServer.LDAP_DEFAULT_ATTRIBUTE_SYNC_POLICY.stringValue());
		ExpandableComposite attSyncPolicyDescriptionExpandable = createDescriptionExpandable(
											parent, 
											Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.attributeSyncPolicyDescription")); //$NON-NLS-1$
		gd = new GridData();
		gd.widthHint = 500;
		attSyncPolicyDescriptionExpandable.setLayoutData(gd);


		Composite syncWrapper = new Composite(parent, SWT.NONE);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		syncWrapper.setLayout(gLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 20;
		syncWrapper.setLayoutData(gd);
		
		new Label(syncWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.syncEntryDNLabel")); //$NON-NLS-1$
		selectionCriteria = new LDAPAttributeSet();
		selectionCriteria.createAttribute("objectClass", CollectionUtil.createHashSet("person", "posixAccount")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		syncDNselector = new LDAPEntrySelectorComposite(syncWrapper, SWT.NONE, selectionCriteria);
		syncDNselector.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.syncDNTextTooltip")); //$NON-NLS-1$
		syncDNselector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncDNselector.setLdapConnectionParamsProvider(((CreateLDAPServerWizardHop) getWizardHop()).getLDAPConnectionParamsProvider());
		
		new Label(syncWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.syncPasswordLabel")); //$NON-NLS-1$
		syncPasswordText = new Text(syncWrapper, SWT.BORDER | SWT.PASSWORD);
		syncPasswordText.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.syncPasswordTextTooltip")); //$NON-NLS-1$
		syncPasswordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(parent);
		return parent;
	}

	/**
	 * Get LDAP entry name used during synchronization process
	 * 
	 * @return LDAP entry full name or <code>null</code> if not set
	 */
	public String getSyncDN() {
		if (syncDNselector != null){
			return syncDNselector.getEntryName();
		}
		return null;
	}
	
	/**
	 * Password for synchronization
	 * 
	 * @return password as {@link String} or <code>null</code> if not set
	 */
	public String getSyncPassword() {
		if (syncPasswordText != null){
			return syncPasswordText.getText();
		}
		return null;
	}
	
	/**
	 * Get leading state
	 * 
	 * @return <code>true</code> if this {@link LDAPServer} is a leading system
	 */
	public boolean getLeadingState(){
		if (isLeadingButton != null){
			return isLeadingButton.getSelection();
		}
		return false;
	}
	
	/**
	 * Get base entry DN (see {@link LDAPServer#setBaseDN(String)})
	 * 
	 * @return name of base entry 
	 */
	public String getBaseEntryDN(){
		if (ldapEntrySelector != null){
			return ldapEntrySelector.getEntryName();
		}
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Get selected {@link LDAPAttributeSyncPolicy}, see {@link AttributeStructFieldSyncHelper} for details
	 * 
	 * @return selected value of {@link LDAPAttributeSyncPolicy}
	 */
	public LDAPAttributeSyncPolicy getAttributeSyncPolicy(){
		return LDAPAttributeSyncPolicy.findAttributeSyncPolicyByStringValue(attributeSyncPolicyCombo.getText());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPageComplete() {
		if (ldapEntrySelector != null 
				&& !ldapEntrySelector.isDisposed()
				&& !"".equals(ldapEntrySelector.getEntryName())){ //$NON-NLS-1$
			return true;
		}
		return false;
	}
	
	private ExpandableComposite createDescriptionExpandable(Composite parent, String descriptionText){
		// For some reason ExpandableComposite is shown with a nasty horizontal scrollbar and with no CLIENT_INDENT
		// if it's created with a common widget constructor (Windows 7 x64 SP1 machine, SWT plugin 3.6.0 x86). 
		// That's why I'm using a FormToolkit instance here to create an ExpandableComposite with its help. Denis.
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		toolkit.setBackground(getShell().getBackground());
		toolkit.setBorderStyle(SWT.BORDER);
		ExpandableComposite descriptionExpandable = toolkit.createExpandableComposite(
				parent, ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				);
		descriptionExpandable.marginHeight = -2;
		descriptionExpandable.clientVerticalSpacing = 5;
		descriptionExpandable.titleBarTextMarginWidth = -2;
		descriptionExpandable.setLayout(new FillLayout());
		descriptionExpandable.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerAdvancedConfigWizardPage.descriptionExpandableLabel")); //$NON-NLS-1$
		
		Label descriptionLabel = new Label(descriptionExpandable, SWT.WRAP);
		descriptionLabel.setText(descriptionText);
		
		descriptionExpandable.setClient(descriptionLabel);
		descriptionExpandable.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				((Control) e.getSource()).getParent().layout();
			}
		});
		
		return descriptionExpandable;
	}

}
