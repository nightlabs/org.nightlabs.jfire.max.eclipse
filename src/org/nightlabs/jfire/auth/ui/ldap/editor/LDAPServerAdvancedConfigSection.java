package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.auth.ui.ldap.LDAPEntrySelectorComposite;
import org.nightlabs.jfire.auth.ui.ldap.LDAPEntrySelectorComposite.BindCredentials;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.NamedScript;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSetDAO;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
import org.nightlabs.jfire.base.security.integration.ldap.sync.AttributeStructFieldSyncHelper.LDAPAttributeSyncPolicy;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.jfire.security.NoUserException;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.UserDescriptor;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * Section of {@link LDAPServerEditorMainPage} for editing advanced {@link LDAPServer} properties:
 * leading state, entry name and password used for synchronization, JFire and LDAP interaction scripts.
 * 
 * Does not work directly with edited {@link LDAPServer} instance but with {@link LDAPServerAdvancedConfigModel}
 * which wraps around it and performs model specific actions.
 *  
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerAdvancedConfigSection extends ToolBarSectionPart {
	
	private Button isLeadingButton;
	private LDAPEntrySelectorComposite syncDNSelector;
	private Text syncPasswordText;
	private CCombo attributeSyncPolicyCombo;

	private LDAPServerAdvancedConfigModel model;
	
	/**
	 * Page reference is held here for making calls to {@link LDAPServerEditorScriptSetPage}.
	 * See {@link #openScriptPageSelectionListener}.
	 */
	private IFormPage advancedConfigPage;

	/**
	 * Set to <code>true</code> while automatic refreshing of UI elements
	 * happens. Some listeners are enabled at this time.
	 */
	private boolean refreshing = false;

	ModifyListener dirtyModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			if (!refreshing){
				markDirty();
			}
		}
	};
	SelectionListener dirtySelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent selectionevent) {
			if (!refreshing){
				markDirty();
			}
		};
	};
	IExpansionListener descriptionExpansionListener = new ExpansionAdapter() {
		public void expansionStateChanged(ExpansionEvent e) {
			((Control) e.getSource()).getParent().layout();
		}
	};

	
	public LDAPServerAdvancedConfigSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Advanced"); //$NON-NLS-1$);
		this.advancedConfigPage = page;
		createContents(getSection(), page.getEditor().getToolkit());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		if (!(input instanceof LDAPServer)){
			throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.illegalInputExceptionText")); //$NON-NLS-1$
		}
		this.model = new LDAPServerAdvancedConfigModel((LDAPServer) input);
		return super.setFormInput(input);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		refreshing = true;
		try{
			if (model != null){
				isLeadingButton.setSelection(model.isLeading());
				syncDNSelector.setEntryName(model.getSyncDN());
				syncDNSelector.setLdapConnectionParamsProvider(model.getLdapServer());
				setBindCredentialsToSelector(syncDNSelector);
				syncPasswordText.setText(model.getSyncPassword());
				attributeSyncPolicyCombo.setItems(LDAPAttributeSyncPolicy.getPossibleAttributeSyncPolicyValues());
				attributeSyncPolicyCombo.setText(model.getAttributeSyncPolicy());
			}
		}finally{
			refreshing = false;
		}
		super.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(boolean onSave) {
		if (model != null){
			model.setLeading(isLeadingButton.getSelection());
			model.setSyncDN(syncDNSelector.getEntryName());
			model.setSyncPassword(syncPasswordText.getText());
			model.setAttributeSyncPolicy(attributeSyncPolicyCombo.getText());
		}
		super.commit(onSave);
	}
	
	private void createContents(Section section, FormToolkit toolkit){
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite parent = EntityEditorUtil.createCompositeClient(toolkit, section, 2);
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		parentLayout.verticalSpacing = 10;
		parentLayout.horizontalSpacing = 10;
		parentLayout.marginTop = 10;
		parentLayout.marginRight = 20;
		
		isLeadingButton = toolkit.createButton(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.isLeadingCheckButtonLabel"), SWT.CHECK | SWT.FLAT); //$NON-NLS-1$
		isLeadingButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.isLeadingCheckButtonTooltip")); //$NON-NLS-1$
		isLeadingButton.addSelectionListener(dirtySelectionListener);
		isLeadingButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		createDescriptionExpandable(
				parent, toolkit, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.leadingSystemDescription")); //$NON-NLS-1$
		
		
		Label attributeSyncTypeLabel = toolkit.createLabel(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.attributeSycnPolicyLabel"), SWT.NONE); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		attributeSyncTypeLabel.setLayoutData(gd);
		attributeSyncPolicyCombo = new CCombo(parent, toolkit.getBorderStyle() | SWT.READ_ONLY);
		attributeSyncPolicyCombo.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.attributeSyncPolicyComboTooltip")); //$NON-NLS-1$
		attributeSyncPolicyCombo.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		attributeSyncPolicyCombo.addSelectionListener(dirtySelectionListener);
		createDescriptionExpandable(
				parent, toolkit, 
				Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.attributeSyncPolicyDescription")); //$NON-NLS-1$


		Composite syncWrapper = toolkit.createComposite(parent, SWT.NONE);
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
		
		toolkit.createLabel(syncWrapper, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.syncEntryDNLabel"), SWT.NONE); //$NON-NLS-1$
		LDAPAttributeSet selectionCriteria = new LDAPAttributeSet();
		selectionCriteria.createAttribute("objectClass", CollectionUtil.createHashSet("person", "posixAccount")); //$NON-NLS-1$ //$NON-NLS-2$
		syncDNSelector = new LDAPEntrySelectorComposite(syncWrapper, SWT.NONE, selectionCriteria);
		syncDNSelector.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.syncDNTextTooltip")); //$NON-NLS-1$
		syncDNSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncDNSelector.addModifyListener(dirtyModifyListener);
		
		toolkit.createLabel(syncWrapper, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.syncPasswordLabel"), SWT.NONE); //$NON-NLS-1$
		syncPasswordText = toolkit.createText(syncWrapper, "", toolkit.getBorderStyle() | SWT.PASSWORD); //$NON-NLS-1$
		syncPasswordText.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.syncPasswordTextTooltip")); //$NON-NLS-1$
		syncPasswordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncPasswordText.addModifyListener(dirtyModifyListener);


		Label separatorLabel = toolkit.createLabel(parent, "", SWT.SEPARATOR | SWT.HORIZONTAL); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 20;
		separatorLabel.setLayoutData(gd);
		
		
		Label scriptsLabel = toolkit.createLabel(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.editScriptsLabel"), SWT.NONE); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		scriptsLabel.setLayoutData(gd);
		
		for (NamedScript namedScript : LDAPScriptSetHelper.getNamedScripts()){
			createEditScriptLink(parent, namedScript.getScriptID(), namedScript.getScriptName());
			createDescriptionExpandable(parent, toolkit, LDAPScriptSetHelper.getScriptDescriptionByID(namedScript.getScriptID()));
		}


		separatorLabel = toolkit.createLabel(parent, "", SWT.SEPARATOR | SWT.HORIZONTAL); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		separatorLabel.setLayoutData(gd);
		
		Button openExportButton = new Button(parent, SWT.PUSH);
		openExportButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.openExportButtonLabel")); //$NON-NLS-1$
		openExportButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.openExportButtonTooltip")); //$NON-NLS-1$
		openExportButton.setAlignment(SWT.LEFT);
		openExportButton.setImage(
				SharedImages.getSharedImage(LdapUIPlugin.sharedInstance(), LDAPServerAdvancedConfigSection.class, "exportButton", ImageDimension._16x16.toString(), ImageFormat.png)); //$NON-NLS-1$
		gd = new GridData();
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		openExportButton.setLayoutData(gd);
		openExportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openWizardDialog(model.getLdapServer(), SyncDirection.EXPORT);
			}
		});

		Button openImportButton = new Button(parent, SWT.PUSH);
		openImportButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.openImportButtonLabel")); //$NON-NLS-1$
		openImportButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.openImportBurttonTooltip")); //$NON-NLS-1$
		openImportButton.setAlignment(SWT.LEFT);
		openImportButton.setImage(
				SharedImages.getSharedImage(LdapUIPlugin.sharedInstance(), LDAPServerAdvancedConfigSection.class, "importButton", ImageDimension._16x16.toString(), ImageFormat.png)); //$NON-NLS-1$
		gd = new GridData();
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		openImportButton.setLayoutData(gd);
		openImportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openWizardDialog(model.getLdapServer(), SyncDirection.IMPORT);
			}
		});

	}
	
	private boolean credentialsSet = false;
	private void setBindCredentialsToSelector(LDAPEntrySelectorComposite selectorComposite){
		if (credentialsSet){
			return;
		}
		boolean fallToGlobalSyncCredentials = false;
		BindCredentials bindCredentials = null;
		try{
			UserDescriptor userDescriptor = GlobalSecurityReflector.sharedInstance().getUserDescriptor();
			if (User.USER_ID_SYSTEM.equals(userDescriptor.getUserID())){
				fallToGlobalSyncCredentials = true;
			}else{
				String bindPwd = null;
				Object credential = GlobalSecurityReflector.sharedInstance().getCredential();
				if (credential instanceof String){
					bindPwd = (String) credential;
				}else if (credential instanceof char[]){
					bindPwd = new String((char[])credential);
				}else{
					fallToGlobalSyncCredentials = true;
				}
				
				final String bindUser = LDAPScriptSetDAO.sharedInstance().getLDAPEntryName(
						model.getLdapServer().getUserManagementSystemObjectID(), userDescriptor.getUserObjectID(), new NullProgressMonitor());
				final String bindPassword = bindPwd;
				bindCredentials = new BindCredentials() {
					@Override
					public String getPassword() {
						return bindPassword;
					}
					@Override
					public String getLogin() {
						return bindUser;
					}
				};
			}
		}catch(NoUserException e){
			// There's no logged in User, so we'll try to bind with syncDN and syncPasswrod
			fallToGlobalSyncCredentials = true;
		}
		
		if (fallToGlobalSyncCredentials){
			final String globalUser = syncDNSelector.getEntryName();
			final String globalPwd = syncPasswordText.getText();
			bindCredentials = new BindCredentials() {
				@Override
				public String getPassword() {
					return globalPwd;
				}
				@Override
				public String getLogin() {
					return globalUser;
				}
			};
		}
		
		selectorComposite.setBindCredentials(bindCredentials);
		credentialsSet = true;
	}
	
	private static void openWizardDialog(LDAPServer ldapServer, SyncDirection syncDirection){
		ImportExportWizard wiz = new ImportExportWizard();
		DynamicPathWizardDialog dynamicPathWizardDialog = new DynamicPathWizardDialog(wiz.getShell(), wiz) {
			@Override
			protected Point getInitialSize() {
				return new Point(780,650);
			}
		};
		dynamicPathWizardDialog.setBlockOnOpen(false);
		dynamicPathWizardDialog.open();
		wiz.proceedToSynchronizationPage(ldapServer, syncDirection);
	}
	
	private void createEditScriptLink(Composite parent, String scriptID, String scriptName){
		Link syncToJFireScriptLink = new Link(parent, SWT.NONE);
		syncToJFireScriptLink.setText("<A>"+scriptName+"</A>"); //$NON-NLS-1$ //$NON-NLS-2$
		syncToJFireScriptLink.addSelectionListener(openScriptPageSelectionListener);
		syncToJFireScriptLink.setData(scriptID);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 140;
		gd.verticalIndent = 3;
		syncToJFireScriptLink.setLayoutData(gd);
	}
	
	private ExpandableComposite createDescriptionExpandable(Composite parent, FormToolkit toolkit, String descriptionText){
		ExpandableComposite descriptionExpandable = toolkit.createExpandableComposite(
				parent, ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				);
		descriptionExpandable.setLayout(new FillLayout());
		descriptionExpandable.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerAdvancedConfigSection.descriptionExpandableLabel")); //$NON-NLS-1$
		
		Label descriptionLabel = toolkit.createLabel(descriptionExpandable, descriptionText, SWT.WRAP);
		
		descriptionExpandable.setClient(descriptionLabel);
		descriptionExpandable.addExpansionListener(descriptionExpansionListener);
		GridData gd = new GridData();
		gd.widthHint = 500;
		descriptionExpandable.setLayoutData(gd);
		
		return descriptionExpandable;
	}
	
	/**
	 * This lister finds {@link LDAPServerEditorScriptSetPage}, makes it active and selects a tab item with needed script.
	 */
	private SelectionListener openScriptPageSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent selectionevent) {
			if (selectionevent.getSource() instanceof Link){
				
				IFormPage scriptSetPage = advancedConfigPage.getEditor().setActivePage(LDAPServerEditorScriptSetPage.ID_PAGE);
				String scriptID = (String) ((Widget) selectionevent.getSource()).getData();
				
				if (scriptID != null
						&& !scriptID.isEmpty()
						&& scriptSetPage instanceof LDAPServerEditorScriptSetPage){
					
					LDAPServerScriptSetSection scriptsSection = ((LDAPServerEditorScriptSetPage) scriptSetPage).getScriptsSection();
					if (scriptsSection != null
							&& scriptsSection.getContainer() != null
							&& !scriptsSection.getContainer().isDisposed()){
						
						scriptsSection.setActiveScriptTab(scriptID);
					}
				}
				
			}
		};
	};
	
}
