package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;

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
		

		Composite syncWrapper = toolkit.createComposite(parent, SWT.NONE);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		syncWrapper.setLayout(gLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		syncWrapper.setLayoutData(gd);
		
		toolkit.createLabel(syncWrapper, "Sync entry DN:", SWT.NONE);
		syncDNText = toolkit.createText(syncWrapper, "", toolkit.getBorderStyle());
		syncDNText.setToolTipText("Distingueshed name of LDAP entry which is used for binding \nduring synchronization between LDAP directory and JFire");
		syncDNText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncDNText.addModifyListener(dirtyModifyListener);
		
		toolkit.createLabel(syncWrapper, "Sync password:", SWT.NONE);
		syncPasswordText = toolkit.createText(syncWrapper, "", toolkit.getBorderStyle());
		syncPasswordText.setToolTipText("Password of LDAP entry which is used for binding \nduring synchronization between LDAP directory and JFire");
		syncPasswordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		syncPasswordText.addModifyListener(dirtyModifyListener);

		
		Label scriptsLabel = toolkit.createLabel(parent, "Edit scripts for configuring LDAP and JFire interaction:", SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		scriptsLabel.setLayoutData(gd);
		
		for (String scriptName : LDAPScriptSetHelper.getAllScriptNames()){
			createEditScriptButton(parent, toolkit, scriptName);
			createDescriptionExpandable(parent, toolkit, LDAPScriptSetHelper.getScriptDescriptionByName(scriptName));
		}

	}
	
	private void createEditScriptButton(Composite parent, FormToolkit toolkit, String scriptName){
		Button syncToJFireScriptButton = toolkit.createButton(parent, scriptName+"...", SWT.FLAT);
		syncToJFireScriptButton.addSelectionListener(openScriptPageSelectionListener);
		syncToJFireScriptButton.setData(scriptName);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 200;
		syncToJFireScriptButton.setLayoutData(gd);
	}
	
	private void createDescriptionExpandable(Composite parent, FormToolkit toolkit, String descriptionText){
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
	}
	
	/**
	 * This lister finds {@link LDAPServerEditorScriptSetPage}, makes it active and selects a tab item with needed script.
	 */
	private SelectionListener openScriptPageSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent selectionevent) {
			if (selectionevent.getSource() instanceof Button){
				
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
