package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.ModificationFinishedEvent;
import org.nightlabs.base.ui.language.ModificationFinishedListener;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.AuthenticationMethod;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.EncryptionMethod;

/**
 * Section of {@link LDAPServerEditorMainPage} for editing general {@link LDAPServer} properties:
 * host, port, name, description, active state, encryption and authentication methods.
 * 
 * Does not work directly with edited {@link LDAPServer} instance but with {@link LDAPServerGeneralConfigModel}
 * which wraps around it and performs model specific actions.
 *  
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerGeneralConfigSection extends ToolBarSectionPart {

	private Text hostText;
	private Text portText;
	private I18nTextEditor nameText;
	private I18nTextEditorMultiLine descriptionText;
	private Button isActiveButton;
	private CCombo encryptionMethodCombo;
	private CCombo authMethodCombo;
	
	private LDAPServerGeneralConfigModel model;

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
	ModificationFinishedListener dirtyModificationFinishedListener = new ModificationFinishedListener() {
		@Override
		public void modificationFinished(ModificationFinishedEvent event) {
			if (!refreshing){
				markDirty();
			}
		}
	};


	public LDAPServerGeneralConfigSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "General"); //$NON-NLS-1$);
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
		this.model = new LDAPServerGeneralConfigModel((LDAPServer) input);
		return super.setFormInput(input);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(boolean onSave) {
		if (model != null){
			model.setActive(isActiveButton.getSelection());
			model.setHost(hostText.getText());
			model.setPort(portText.getText());
			model.setName(nameText.getI18nText());
			model.setDescription(descriptionText.getI18nText());
			model.setEncryptionMethod(encryptionMethodCombo.getText());
			model.setAuthenticationMethod(authMethodCombo.getText());
		}
		super.commit(onSave);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		refreshing = true;
		try{
			if (model != null){
				isActiveButton.setSelection(model.isActive());
				hostText.setText(model.getHost());
				portText.setText(model.getPort());
				nameText.setI18nText(model.getName(), EditMode.BUFFERED);
				descriptionText.setI18nText(model.getDescription(), EditMode.BUFFERED);
				encryptionMethodCombo.setItems(EncryptionMethod.getPossibleEncryptionMethods());
				encryptionMethodCombo.setText(model.getEncryptionMethod());
				authMethodCombo.setItems(AuthenticationMethod.getPossibleAuthenticationMethods());
				authMethodCombo.setText(model.getAuthenticationMethod());
			}
		}finally{
			refreshing = false;
		}
		super.refresh();
	}

	private void createContents(Section section, FormToolkit toolkit){
		
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite parent = EntityEditorUtil.createCompositeClient(toolkit, section, 2);
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		parentLayout.makeColumnsEqualWidth = true;
		parentLayout.verticalSpacing = 10;
		parentLayout.marginTop = 10;
		parentLayout.marginRight = 20;
		
		isActiveButton = toolkit.createButton(parent, "is active and used for login", SWT.CHECK | SWT.FLAT);
		isActiveButton.setToolTipText("Indicates if bind operation will be called against this LDAP server on login");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		isActiveButton.setLayoutData(gd);
		isActiveButton.addSelectionListener(dirtySelectionListener);
		

		Composite leftWrapper = toolkit.createComposite(parent, SWT.NONE);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		leftWrapper.setLayout(gLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		leftWrapper.setLayoutData(gd);
		
		toolkit.createLabel(leftWrapper, "Host:", SWT.NONE);
		hostText = toolkit.createText(leftWrapper, "", toolkit.getBorderStyle());
		hostText.setToolTipText(String.format("Specify host to connect to LDAP directory. \nDefault (%s) will be used if empty.", LDAPServer.LDAP_DEFAULT_HOST));
		hostText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostText.addModifyListener(dirtyModifyListener);
		
		toolkit.createLabel(leftWrapper, "Port:", SWT.NONE);
		portText = toolkit.createText(leftWrapper, "", toolkit.getBorderStyle());
		portText.setToolTipText(String.format("Specify port to connect to LDAP directory. \nDefault (%s) will be used if empty", LDAPServer.LDAP_DEFAULT_PORT));
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portText.addModifyListener(dirtyModifyListener);

		Composite rightWrapper = toolkit.createComposite(parent, SWT.NONE);
		gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		gLayout.marginLeft = 50;
		rightWrapper.setLayout(gLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		rightWrapper.setLayoutData(gd);

		toolkit.createLabel(rightWrapper, "Encryption method:", SWT.NONE);
		encryptionMethodCombo = new CCombo(rightWrapper, toolkit.getBorderStyle() | SWT.READ_ONLY);
		encryptionMethodCombo.setToolTipText("Changes encryption method used during communication with LDAP directory");
		encryptionMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		encryptionMethodCombo.addSelectionListener(dirtySelectionListener);

		toolkit.createLabel(rightWrapper, "Authentication method:", SWT.NONE);
		authMethodCombo = new CCombo(rightWrapper, toolkit.getBorderStyle() | SWT.READ_ONLY);
		authMethodCombo.setToolTipText("Changes authentication method used during binding against LDAP directory");
		authMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		authMethodCombo.addSelectionListener(dirtySelectionListener);

		
		nameText = new I18nTextEditor(parent, "Name:");
		nameText.setToolTipText("Name this LDAP server in different languages");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		nameText.setLayoutData(gd);
		nameText.addModifyListener(dirtyModifyListener);
		nameText.addModificationFinishedListener(dirtyModificationFinishedListener);
		
		descriptionText = new I18nTextEditorMultiLine(parent, "Description:");
		descriptionText.setToolTipText("Describe this LDAP server in different languages");
		descriptionText.setVisibleLineCount(I18nTextEditorMultiLine.DEFAULT_LINECOUNT + 2);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		descriptionText.setLayoutData(gd);
		descriptionText.addModifyListener(dirtyModifyListener);
		descriptionText.addModificationFinishedListener(dirtyModificationFinishedListener);
		
	}

}
