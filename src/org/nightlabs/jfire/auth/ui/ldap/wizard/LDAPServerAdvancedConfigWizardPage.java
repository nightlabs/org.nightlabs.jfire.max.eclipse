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
	private Text syncDNText;
	private Text syncPasswordText;
	private LDAPEntrySelectorComposite ldapEntrySelector;
	private CCombo attributeSyncPolicyCombo;

	/**
	 * Default constructor
	 */
	public LDAPServerAdvancedConfigWizardPage() {
		super(LDAPServerAdvancedConfigWizardPage.class.getName(), "Configure LDAP server", SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerAdvancedConfigWizardPage.class));
		setDescription("LDAP server advanced configuration");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		
		new Label(parent, SWT.NONE).setText("Base entry name:");
		LDAPAttributeSet selectionCriteria = new LDAPAttributeSet();
		selectionCriteria.createAttribute("objectClass", CollectionUtil.createHashSet("organizationalUnit"));
		ldapEntrySelector = new LDAPEntrySelectorComposite(parent, SWT.NONE, selectionCriteria);
		ldapEntrySelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ldapEntrySelector.setLdapConnectionParamsProvider(((CreateLDAPServerWizardHop) getWizardHop()).getLDAPConnectionParamsProvider());
		ldapEntrySelector.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyevent) {
				String selectedEntryName = ldapEntrySelector.getEntryName();
				if (selectedEntryName == null || selectedEntryName.isEmpty()){
					updateStatus("Please select base LDAP entry!");
				}else{
					updateStatus(null);
				}
			}
		});
		
		String baseEntryDescription = "Name of LDAP entry which is used as base for constructing LDAP distingueshed names from JFire objects data " +
				"(e.g. userID). It means that all LDAP users (entries) which are supposed to be used in JFire-LDAP interaction (including login) should " +
				"be located directly unser this base entry. Base entry can be changed later or even more than one base entry could be added by editing " +
				"JFire-LDAP interaction scripts within LDAPServer editor.";
		ExpandableComposite baseEntryDescriptionExpandable = createDescriptionExpandable(parent, baseEntryDescription);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.widthHint = 500;
		baseEntryDescriptionExpandable.setLayoutData(gd);

		
		Label separatorLdabel = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separatorLdabel.setLayoutData(gd);
		
		
		isLeadingButton = new Button(parent, SWT.CHECK);
		isLeadingButton.setText("is a leading system");
		isLeadingButton.setToolTipText("If LDAP is a leading system than all user modifications are synchronized from LDAP directory to JFire. \nOtherwise it's done other way around.");
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.verticalIndent = 10;
		isLeadingButton.setLayoutData(gd);
		
		String leadingSystemDescription = "If LDAP is a leading system (and this checkbox is on) than all users are maintained in LDAP directory and modifications are synchronized to JFire. " +
											"It's done in several ways: during user login (if it exists in LDAP but not in JFire), with timer task which launches every hour and " +
											"(not supported yet!) with push notifications from LDAP server.\n" +
											"If JFire is a leading system than all users are maintained inside JFire and modifications are synchronized to LDAP directory. " +
											"It's done just after modification is stored to the database with the help of JDO Lifecycle listeners.";
		ExpandableComposite leadingSystemDescriptionExpandable = createDescriptionExpandable(parent, leadingSystemDescription);
		gd = new GridData();
		gd.widthHint = 500;
		gd.verticalIndent = 10;
		leadingSystemDescriptionExpandable.setLayoutData(gd);
		
		
		Label attSyncLabel = new Label(parent, SWT.NONE);
		attSyncLabel.setText("Attribute sync policy:");
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		attSyncLabel.setLayoutData(gd);
		
		attributeSyncPolicyCombo = new CCombo(parent, SWT.BORDER);
		attributeSyncPolicyCombo.setEditable(false);
		attributeSyncPolicyCombo.setToolTipText("Changes method which is used when synchronizing LDAP attributes into Person datafields, see description for more details");
		attributeSyncPolicyCombo.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		attributeSyncPolicyCombo.setItems(LDAPAttributeSyncPolicy.getPossibleAttributeSyncPolicyValues());
		attributeSyncPolicyCombo.setText(LDAPServer.LDAP_DEFAULT_ATTRIBUTE_SYNC_POLICY.stringValue());
		ExpandableComposite attSyncPolicyDescriptionExpandable = createDescriptionExpandable(
											parent, 
											"LDAP attributes could be mapped to Person datafields in different ways: ALL - every attribute from LDAP schema of this server " +
											"will be mapped to Person datafields and all needed structures will be created, MANDATORY ONLY - only attributes which are " +
											"mandatory by LDAP schema will be mapped, others are mapped inside corresponding script, NONE - nothing will be mapped by the " +
											"system, all mapping will occur inside script which could be edited by administrator.");
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
		
		new Label(syncWrapper, SWT.NONE).setText("Sync entry DN:");
		syncDNText = new Text(syncWrapper, SWT.BORDER);
		syncDNText.setToolTipText("Distingueshed name of LDAP entry which is used for binding \nduring synchronization between LDAP directory and JFire");
		syncDNText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(syncWrapper, SWT.NONE).setText("Sync password:");
		syncPasswordText = new Text(syncWrapper, SWT.BORDER | SWT.PASSWORD);
		syncPasswordText.setToolTipText("Password of LDAP entry which is used for binding \nduring synchronization between LDAP directory and JFire");
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
		if (syncDNText != null){
			return syncDNText.getText();
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
		return "";
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
				&& !"".equals(ldapEntrySelector.getEntryName())){
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
		descriptionExpandable.setText("See description");
		
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
