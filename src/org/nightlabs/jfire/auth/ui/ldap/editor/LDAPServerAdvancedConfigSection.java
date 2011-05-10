package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.jface.dialogs.MessageDialog;
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
	
	private static final int DESCRIPTION_EXPANDABLE_STYLE = ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
	private static final int SCRIPT_DESCRIPTION_WIDTH = 500;
	private static final int SCRIPT_BUTTON_WIDTH = 200;
	
	private Button isLeadingButton;
	private Text syncDNText;
	private Text syncPasswordText;

	private LDAPServerAdvancedConfigModel model;

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


		createEditScriptButton(parent, toolkit, "Edit bind variables script...");
		String commonBindScriptDescription = "Used for binding script variables to values taken from JFire objects (e.g. User and Person)." +
				"These variables are used afterwards in other scripts. Java objects for User and Person " +
				"are available inside this script. NOT supposed to return any values.";
		createDescriptionExpandable(parent, toolkit, commonBindScriptDescription);

		
		createEditScriptButton(parent, toolkit, "Edit get entry name script...");
		String getEntryNameScriptDescription = "Used for generating a String with an LDAP entry Distingueshed Name which is built usign User and/or Person object's data. " +
				"Should return a String with an LDAP entry DN on the last line of the script.";
		createDescriptionExpandable(parent, toolkit, getEntryNameScriptDescription);

		
		createEditScriptButton(parent, toolkit, "Edit get attribute set script...");
		String generateAttributesScriptDescription = "Used for generating a LDAPAttributeSet with attributes names and values which are then passed to LDAP modidifcation calls." +
				"Such modifications happen during synchronization of user data from JFire to LDAP directory when JFire is a leading system." +
				"This script makes use of variables from bind variables script. Should return LDAPAttributeSet on the last line of the script.";
		createDescriptionExpandable(parent, toolkit, generateAttributesScriptDescription);
		
		
		createEditScriptButton(parent, toolkit, "Edit get parent entries script...");
		String getParentScriptDescription =	"Used for generating a List with names of LDAP entries which are parents to all LDAP user entries which should be synchronized. " +
				"These parent entries are queried during synchronization when LDAPServer is a leading system. Should return an ArrayList of Strings " +
				"with entries names on the last line of the script.";
		createDescriptionExpandable(parent, toolkit, getParentScriptDescription);

		
		createEditScriptButton(parent, toolkit, "Edit sync to JFire script...");
		String syncScriptDescription = "Used for storing data into JFire objects (User and/or Person) during synchronization when LDAPServer is a leading system. " +
				"It makes use of several java objects : allAtributes - LDAPAttributeSet with all attributes of LDAP entry to be synchronized, " +
				"pm - PersistenceManager, organisationID - the ID of JFire organisation, newPersonID - value returned by " +
				"IDGenerator.nextID(PropertySet.class) used when new Person object is created, logger - org.slf4j.Logger for debug purposes. " +
				"Should return persisted object (either User or Person) on the last line of the script.";
		createDescriptionExpandable(parent, toolkit, syncScriptDescription);		

	}
	
	private void createEditScriptButton(Composite parent, FormToolkit toolkit, String buttonText){
		Button syncToJFireScriptButton = toolkit.createButton(parent, buttonText, SWT.FLAT);
		syncToJFireScriptButton.addSelectionListener(tempLinkSelectionListener);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = SCRIPT_BUTTON_WIDTH;
		syncToJFireScriptButton.setLayoutData(gd);
	}
	
	private void createDescriptionExpandable(Composite parent, FormToolkit toolkit, String descriptionText){
		ExpandableComposite descriptionExpandable = toolkit.createExpandableComposite(
				parent, DESCRIPTION_EXPANDABLE_STYLE
				);
		descriptionExpandable.setLayout(new FillLayout());
		descriptionExpandable.setText("See description");
		
		Label descriptionLabel = toolkit.createLabel(descriptionExpandable, descriptionText, SWT.WRAP);
		
		descriptionExpandable.setClient(descriptionLabel);
		descriptionExpandable.addExpansionListener(descriptionExpansionListener);
		GridData gd = new GridData();
		gd.widthHint = SCRIPT_DESCRIPTION_WIDTH;
		descriptionExpandable.setLayoutData(gd);
	}
	
	private SelectionListener tempLinkSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent selectionevent) {
			MessageDialog.openInformation(null, "Script Editor missing!", "Sorry! Small script editor will be added soon.");
		};
	};
	
}
