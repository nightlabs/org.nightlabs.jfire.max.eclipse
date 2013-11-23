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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
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
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.AuthenticationMethod;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.EncryptionMethod;
import org.nightlabs.jfire.base.security.integration.ldap.scripts.ILDAPScriptProvider;

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
	 * See {@link LDAPServerEditorMainPage#openScriptPageSelectionListener}.
	 */
	private SelectionListener openScriptSelectionListener;

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


	public LDAPServerGeneralConfigSection(IFormPage page, Composite parent, SelectionListener openScriptListener) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.sectionTitle")); //$NON-NLS-1$
		this.openScriptSelectionListener = openScriptListener;
		createContents(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		if (!(input instanceof LDAPServer)){
			throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.illegalInputExceptionText")); //$NON-NLS-1$
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
		
		isActiveButton = toolkit.createButton(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.isActiveCheckButtonLabel"), SWT.CHECK | SWT.FLAT); //$NON-NLS-1$
		isActiveButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.isActiveCheckButtonTooltip")); //$NON-NLS-1$
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
		
		toolkit.createLabel(leftWrapper, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.hostLabel"), SWT.NONE); //$NON-NLS-1$
		hostText = toolkit.createText(leftWrapper, "", toolkit.getBorderStyle()); //$NON-NLS-1$
		hostText.setToolTipText(String.format(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.hostTextTooltip"), LDAPServer.LDAP_DEFAULT_HOST)); //$NON-NLS-1$
		hostText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostText.addModifyListener(dirtyModifyListener);
		
		toolkit.createLabel(leftWrapper, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.portLabel"), SWT.NONE); //$NON-NLS-1$
		portText = toolkit.createText(leftWrapper, "", toolkit.getBorderStyle()); //$NON-NLS-1$
		portText.setToolTipText(String.format(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.portTextTooltip"), LDAPServer.LDAP_DEFAULT_PORT)); //$NON-NLS-1$
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

		toolkit.createLabel(rightWrapper, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.encryptionMethodLabel"), SWT.NONE); //$NON-NLS-1$
		encryptionMethodCombo = new CCombo(rightWrapper, toolkit.getBorderStyle() | SWT.READ_ONLY);
		encryptionMethodCombo.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.encryptionMethodComboTooltip")); //$NON-NLS-1$
		encryptionMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		encryptionMethodCombo.addSelectionListener(dirtySelectionListener);

		toolkit.createLabel(rightWrapper, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.authMethodLabel"), SWT.NONE); //$NON-NLS-1$
		authMethodCombo = new CCombo(rightWrapper, toolkit.getBorderStyle() | SWT.READ_ONLY);
		authMethodCombo.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.authMethodComboTooltip")); //$NON-NLS-1$
		authMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		authMethodCombo.addSelectionListener(dirtySelectionListener);
		
		final Label emptyLabel = toolkit.createLabel(rightWrapper, ""); //$NON-NLS-1$
		emptyLabel.setVisible(false);
		final Link getAttrsScriptLink = new Link(rightWrapper, SWT.NONE);
		getAttrsScriptLink.setText("<A>"+Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.editSASLRealmLinkText")+"</A>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		getAttrsScriptLink.addSelectionListener(openScriptSelectionListener);
		getAttrsScriptLink.setData(ILDAPScriptProvider.GET_ATTRIBUTE_SET_SCRIPT_ID);
		getAttrsScriptLink.setVisible(false);
		authMethodCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean digestMd5Selected = AuthenticationMethod.SASL_DIGEST_MD5.equals(AuthenticationMethod.findAuthenticationMethodByStringValue(authMethodCombo.getText()));
				emptyLabel.setVisible(digestMd5Selected);
				getAttrsScriptLink.setVisible(digestMd5Selected);
				super.widgetSelected(e);
			}
		});
		authMethodCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (authMethodCombo.getText() == null || authMethodCombo.getText().isEmpty()){
					return;
				}
				boolean digestMd5Selected = AuthenticationMethod.SASL_DIGEST_MD5.equals(AuthenticationMethod.findAuthenticationMethodByStringValue(authMethodCombo.getText()));
				emptyLabel.setVisible(digestMd5Selected);
				getAttrsScriptLink.setVisible(digestMd5Selected);
			}
		});
		
		
		nameText = new I18nTextEditor(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.nameTextLabel")); //$NON-NLS-1$
		nameText.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.nameTextTooltip")); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);
		nameText.addModifyListener(dirtyModifyListener);
		nameText.addModificationFinishedListener(dirtyModificationFinishedListener);
		
		descriptionText = new I18nTextEditorMultiLine(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.descriptionTextLabel")); //$NON-NLS-1$
		descriptionText.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerGeneralConfigSection.descriptionTextTooltip")); //$NON-NLS-1$
		descriptionText.setVisibleLineCount(I18nTextEditorMultiLine.DEFAULT_LINECOUNT + 5);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		descriptionText.setLayoutData(gd);
		descriptionText.addModifyListener(dirtyModifyListener);
		descriptionText.addModificationFinishedListener(dirtyModificationFinishedListener);
		
	}

}
