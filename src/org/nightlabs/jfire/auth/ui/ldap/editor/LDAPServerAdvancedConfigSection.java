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
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.sync.AttributeStructFieldSyncHelper.LDAPAttributeSyncPolicy;

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
	private Text syncDNText;
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
			throw new IllegalArgumentException("Input must be a LDAPServer object!");
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
				syncDNText.setText(model.getSyncDN());
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
			model.setSyncDN(syncDNText.getText());
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
		
		isLeadingButton = toolkit.createButton(parent, "is a leading system", SWT.CHECK | SWT.FLAT);
		isLeadingButton.setToolTipText("If LDAP is a leading system than all user modifications are synchronized from LDAP directory to JFire. \nOtherwise it's done other way around.");
		isLeadingButton.addSelectionListener(dirtySelectionListener);
		isLeadingButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		String leadingSystemDescription = "If LDAP is a leading system (and this checkbox is on) than all users are maintained in LDAP directory and modifications are synchronized to JFire. " +
				"It's done in several ways: during user login (if it exists in LDAP but not in JFire), with timer task which launches every hour and " +
				"(not supported yet!) with push notifications from LDAP server.\n" +
				"If JFire is a leading system than all users are maintained inside JFire and modifications are synchronized to LDAP directory. " +
				"It's done just after modification is stored to the database with the help of JDO Lifecycle listeners.";
		createDescriptionExpandable(parent, toolkit, leadingSystemDescription);
		
		
		Label attributeSyncTypeLabel = toolkit.createLabel(parent, "Attribute sync policy:", SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		attributeSyncTypeLabel.setLayoutData(gd);
		attributeSyncPolicyCombo = new CCombo(parent, toolkit.getBorderStyle() | SWT.READ_ONLY);
		attributeSyncPolicyCombo.setToolTipText("Changes method which is used when synchronizing LDAP attributes into Person datafields, see description for more details");
		attributeSyncPolicyCombo.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		attributeSyncPolicyCombo.addSelectionListener(dirtySelectionListener);
		createDescriptionExpandable(
				parent, toolkit, 
				"LDAP attributes could be mapped to Person datafields in different ways: ALL - every attribute from LDAP schema of this server " +
				"will be mapped to Person datafields and all needed structures will be created, MANDATORY ONLY - only attributes which are " +
				"mandatory by LDAP schema will be mapped, others are mapped inside corresponding script, NONE - nothing will be mapped by the " +
				"system, all mapping will occur inside script which could be edited by administrator.");


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
		
		toolkit.createLabel(syncWrapper, "Sync entry DN:", SWT.NONE);
		syncDNText = toolkit.createText(syncWrapper, "", toolkit.getBorderStyle());
		syncDNText.setToolTipText("Distingueshed name of LDAP entry which is used for binding \nduring synchronization between LDAP directory and JFire");
		syncDNText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncDNText.addModifyListener(dirtyModifyListener);
		
		toolkit.createLabel(syncWrapper, "Sync password:", SWT.NONE);
		syncPasswordText = toolkit.createText(syncWrapper, "", toolkit.getBorderStyle() | SWT.PASSWORD);
		syncPasswordText.setToolTipText("Password of LDAP entry which is used for binding \nduring synchronization between LDAP directory and JFire");
		syncPasswordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncPasswordText.addModifyListener(dirtyModifyListener);


		Label separatorLabel = toolkit.createLabel(parent, "", SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 20;
		separatorLabel.setLayoutData(gd);
		
		
		Label scriptsLabel = toolkit.createLabel(parent, "Edit scripts for configuring LDAP and JFire interaction:", SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		scriptsLabel.setLayoutData(gd);
		
		for (String scriptName : LDAPScriptSetHelper.getAllScriptNames()){
			createEditScriptLink(parent, scriptName);
			createDescriptionExpandable(parent, toolkit, LDAPScriptSetHelper.getScriptDescriptionByName(scriptName));
		}


		separatorLabel = toolkit.createLabel(parent, "", SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		separatorLabel.setLayoutData(gd);
		
		Button openExportButton = new Button(parent, SWT.PUSH);
		openExportButton.setText("Export data to LDAP...");
		openExportButton.setToolTipText("Opens a wizard for exporting JFire entities to LDAP directory");
		openExportButton.setAlignment(SWT.LEFT);
		openExportButton.setImage(
				SharedImages.getSharedImage(LdapUIPlugin.sharedInstance(), LDAPServerAdvancedConfigSection.class, "exportButton", ImageDimension._16x16.toString(), ImageFormat.png));
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
		openImportButton.setText("Import data from LDAP...");
		openImportButton.setToolTipText("Opens a wizard for importing LDAP entries into JFire objects");
		openImportButton.setAlignment(SWT.LEFT);
		openImportButton.setImage(
				SharedImages.getSharedImage(LdapUIPlugin.sharedInstance(), LDAPServerAdvancedConfigSection.class, "importButton", ImageDimension._16x16.toString(), ImageFormat.png));
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
	
	private void createEditScriptLink(Composite parent, String scriptName){
		Link syncToJFireScriptLink = new Link(parent, SWT.NONE);
		syncToJFireScriptLink.setText("<A>"+scriptName+"</A>");
		syncToJFireScriptLink.addSelectionListener(openScriptPageSelectionListener);
		syncToJFireScriptLink.setData(scriptName);
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
		descriptionExpandable.setText("See description");
		
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
				String scriptName = (String) ((Widget) selectionevent.getSource()).getData();
				
				if (scriptName != null
						&& !scriptName.isEmpty()
						&& scriptSetPage instanceof LDAPServerEditorScriptSetPage){
					
					LDAPServerScriptSetSection scriptsSection = ((LDAPServerEditorScriptSetPage) scriptSetPage).getScriptsSection();
					if (scriptsSection != null
							&& scriptsSection.getContainer() != null
							&& !scriptsSection.getContainer().isDisposed()){
						
						scriptsSection.setActiveScriptTab(scriptName);
					}
				}
				
			}
		};
	};
	
}
