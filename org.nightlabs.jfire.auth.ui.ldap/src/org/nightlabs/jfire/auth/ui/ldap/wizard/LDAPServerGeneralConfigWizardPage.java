package org.nightlabs.jfire.auth.ui.ldap.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.CreateUserManagementSystemWizard;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.AuthenticationMethod;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider.EncryptionMethod;
import org.nightlabs.util.NLLocale;

/**
 * Wizard page contributed to {@link CreateUserManagementSystemWizard} by {@link CreateLDAPServerWizardHop}.
 * Used for configuring general {@link LDAPServer} properties: host, port, name, description, encryption and 
 * authentication methods. 
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerGeneralConfigWizardPage extends WizardHopPage{

	private Button isActiveButton;
	private Text hostText;
	private Text portText;
	private CCombo encryptionMethodCombo;
	private CCombo authMethodCombo;
	private I18nTextEditor nameText;
	private I18nTextEditorMultiLine descriptionText;

	/**
	 * Default constructor
	 */
	public LDAPServerGeneralConfigWizardPage() {
		super(LDAPServerAdvancedConfigWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.pageTitle"), SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerGeneralConfigWizardPage.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.pageDescription")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		
		isActiveButton = new Button(parent, SWT.CHECK);
		isActiveButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.isActiveCheckButtonLabel")); //$NON-NLS-1$
		isActiveButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.isActiveCheckButtonTooltip")); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		isActiveButton.setLayoutData(gd);
		

		Composite leftWrapper = new Composite(parent, SWT.NONE);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		leftWrapper.setLayout(gLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		leftWrapper.setLayoutData(gd);
		
		new Label(leftWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.hostLabel")); //$NON-NLS-1$
		hostText = new Text(leftWrapper, SWT.BORDER);
		hostText.setToolTipText(String.format(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.hostTextTooltip"), LDAPServer.LDAP_DEFAULT_HOST)); //$NON-NLS-1$
		hostText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostText.setText(LDAPServer.LDAP_DEFAULT_HOST);
		hostText.addModifyListener(defaultModifyListener);
		
		new Label(leftWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.portLabel")); //$NON-NLS-1$
		portText = new Text(leftWrapper, SWT.BORDER);
		portText.setToolTipText(String.format(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.portTextTooltip"), LDAPServer.LDAP_DEFAULT_PORT)); //$NON-NLS-1$
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portText.setText(""+LDAPServer.LDAP_DEFAULT_PORT); //$NON-NLS-1$
		portText.addModifyListener(defaultModifyListener);

		Composite rightWrapper = new Composite(parent, SWT.NONE);
		gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		gLayout.marginLeft = 50;
		rightWrapper.setLayout(gLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		rightWrapper.setLayoutData(gd);

		new Label(rightWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.encryptionMethodLabel")); //$NON-NLS-1$
		encryptionMethodCombo = new CCombo(rightWrapper, SWT.BORDER | SWT.READ_ONLY);
		encryptionMethodCombo.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.encryptionMethodComboTooltip")); //$NON-NLS-1$
		encryptionMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		encryptionMethodCombo.setItems(EncryptionMethod.getPossibleEncryptionMethods());
		encryptionMethodCombo.setText(LDAPServer.LDAP_DEFAULT_ENCRYPTION_METHOD.stringValue());
		
		new Label(rightWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.authMethodLabel")); //$NON-NLS-1$
		authMethodCombo = new CCombo(rightWrapper, SWT.BORDER | SWT.READ_ONLY);
		authMethodCombo.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.authMethodComboTooltip")); //$NON-NLS-1$
		authMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		authMethodCombo.setItems(AuthenticationMethod.getPossibleAuthenticationMethods());
		authMethodCombo.setText(LDAPServer.LDAP_DEFAULT_AUTHENTICATION_METHOD.stringValue());

		
		nameText = new I18nTextEditor(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.nameTextLabel")); //$NON-NLS-1$
		nameText.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.nameTextTooltip")); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		nameText.setLayoutData(gd);
		I18nText defaultLdapName = new I18nTextBuffer();
		defaultLdapName.setText(NLLocale.getDefault(), LDAPServer.LDAP_DEFAULT_SERVER_NAME);
		nameText.setI18nText(defaultLdapName);
		nameText.addModifyListener(defaultModifyListener);
		
		descriptionText = new I18nTextEditorMultiLine(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.descriptionTextLabel")); //$NON-NLS-1$
		descriptionText.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.descriptionTextTooltip")); //$NON-NLS-1$
		descriptionText.setVisibleLineCount(I18nTextEditorMultiLine.DEFAULT_LINECOUNT + 2);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		descriptionText.setLayoutData(gd);

		setControl(parent);
		return parent;
	}

	/**
	 * Get host for {@link LDAPServer}
	 * 
	 * @return host
	 */
	public String getHost() {
		return hostText.getText();
	}
	
	/**
	 * Get port for {@link LDAPServer}
	 * 
	 * @return port
	 */
	public int getPort() {
		return Integer.parseInt(portText.getText());
	}
	
	/**
	 * Get {@link EncryptionMethod} for {@link LDAPServer}
	 * 
	 * @return selected {@link EncryptionMethod}
	 */
	public EncryptionMethod getEncryptionMethod() {
		return EncryptionMethod.findEncryptionMethodByStringValue(encryptionMethodCombo.getText());
	}

	/**
	 * Get {@link AuthenticationMethod} for {@link LDAPServer}. See {@link LDAPServer#setAuthenticationMethod(AuthenticationMethod)}
	 * 
	 * @return selected {@link AuthenticationMethod}
	 */
	public AuthenticationMethod getAuthenticationMethod() {
		return AuthenticationMethod.findAuthenticationMethodByStringValue(authMethodCombo.getText());
	}
	
	/**
	 * Get active state
	 * 
	 * @return <code>true</code> if this {@link LDAPServer} is active and used for login
	 */
	public boolean getActiveState(){
		return isActiveButton.getSelection();
	}
	
	/**
	 * Get name for {@link LDAPServer} as {@link I18nText}
	 * 
	 * @return name
	 */
	public I18nText getLDAPName() {
		return nameText.getI18nText();
	}

	/**
	 * Get description for {@link LDAPServer} as {@link I18nText}
	 * 
	 * @return description
	 */
	public I18nText getLDAPDescription() {
		return descriptionText.getI18nText();
	}

	/**
	 * Checks if page is complete: host, port and name are specified
	 */
	@Override
	public boolean isPageComplete() {
		return checkTextNotEmpty(hostText) 
				&& checkTextNotEmpty(portText) 
				&& (nameText != null && !nameText.isDisposed() && checkTextNotEmpty(nameText));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBeLastPage() {
		return false;
	}
	
	private boolean checkTextNotEmpty(I18nTextEditor text){
		String editText = ""; //$NON-NLS-1$
		try{
			editText = text.getEditText();
		}catch(Exception e){
			// do nothing in case text is disposed or null
		}
		return text != null 
				&& (!text.isDisposed() 
						&& !editText.isEmpty());
	}

	private boolean checkTextNotEmpty(Text text){
		return text != null 
				&& (!text.isDisposed() 
						&& text.getText() != null 
						&& !text.getText().isEmpty());
	}

	private ModifyListener defaultModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			verifyInput();
		}
	};
	
	private boolean verifyInput(){
		String editNameText = ""; //$NON-NLS-1$
		try{
			editNameText = nameText.getEditText();
		}catch(Exception e){
			// do nothing in case text is disposed or null
		}
		if (hostText.getText() == null || "".equals(hostText.getText())){ //$NON-NLS-1$
			updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.pageStatus_noHost")); //$NON-NLS-1$
			return false;
		}else if (portText.getText() == null || "".equals(portText.getText())){ //$NON-NLS-1$
			updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.pageStatus_noPort")); //$NON-NLS-1$
			return false;
		}else if (portText.getText() != null && !portText.getText().isEmpty()){
			try{
				Integer.parseInt(portText.getText());
			}catch(NumberFormatException e){
				updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.pageStatus_portNotInteger")); //$NON-NLS-1$
				return false;
			}
		}else if ("".equals(editNameText)){ //$NON-NLS-1$
			updateStatus(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerGeneralConfigWizardPage.pageStatus_noName")); //$NON-NLS-1$
			return false;
		}
		updateStatus(null);
		return true;
	}
}
