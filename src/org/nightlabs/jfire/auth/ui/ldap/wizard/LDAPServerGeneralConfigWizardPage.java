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
		super(LDAPServerAdvancedConfigWizardPage.class.getName(), "Configure LDAP server", SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerGeneralConfigWizardPage.class));
		setDescription("LDAP server general configuration");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		
		isActiveButton = new Button(parent, SWT.CHECK);
		isActiveButton.setText("is active and used for login");
		isActiveButton.setToolTipText("Indicates if bind operation will be called against this LDAP server on login");
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
		
		new Label(leftWrapper, SWT.NONE).setText("Host:");
		hostText = new Text(leftWrapper, SWT.BORDER);
		hostText.setToolTipText(String.format("Specify host to connect to LDAP directory. \nDefault (%s) will be used if empty.", LDAPServer.LDAP_DEFAULT_HOST));
		hostText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostText.setText(LDAPServer.LDAP_DEFAULT_HOST);
		hostText.addModifyListener(defaultModifyListener);
		
		new Label(leftWrapper, SWT.NONE).setText("Port:");
		portText = new Text(leftWrapper, SWT.BORDER);
		portText.setToolTipText(String.format("Specify port to connect to LDAP directory. \nDefault (%s) will be used if empty", LDAPServer.LDAP_DEFAULT_PORT));
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portText.setText(""+LDAPServer.LDAP_DEFAULT_PORT);
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

		new Label(rightWrapper, SWT.NONE).setText("Encryption method:");
		encryptionMethodCombo = new CCombo(rightWrapper, SWT.BORDER | SWT.READ_ONLY);
		encryptionMethodCombo.setToolTipText("Changes encryption method used during communication with LDAP directory");
		encryptionMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		encryptionMethodCombo.setItems(EncryptionMethod.getPossibleEncryptionMethods());
		encryptionMethodCombo.setText(LDAPServer.LDAP_DEFAULT_ENCRYPTION_METHOD.stringValue());
		
		new Label(rightWrapper, SWT.NONE).setText("Authentication method:");
		authMethodCombo = new CCombo(rightWrapper, SWT.BORDER | SWT.READ_ONLY);
		authMethodCombo.setToolTipText("Changes authentication method used during binding against LDAP directory");
		authMethodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		authMethodCombo.setItems(AuthenticationMethod.getPossibleAuthenticationMethods());
		authMethodCombo.setText(LDAPServer.LDAP_DEFAULT_AUTHENTICATION_METHOD.stringValue());

		
		nameText = new I18nTextEditor(parent, "Name:");
		nameText.setToolTipText("Name this LDAP server in different languages");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		nameText.setLayoutData(gd);
		I18nText defaultLdapName = new I18nTextBuffer();
		defaultLdapName.setText(NLLocale.getDefault(), LDAPServer.LDAP_DEFAULT_SERVER_NAME);
		nameText.setI18nText(defaultLdapName);
		nameText.addModifyListener(defaultModifyListener);
		
		descriptionText = new I18nTextEditorMultiLine(parent, "Description:");
		descriptionText.setToolTipText("Describe this LDAP server in different languages");
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
				&& (nameText != null && !nameText.isDisposed() && checkTextNotEmpty(nameText.getText()));
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
		if (hostText.getText() == null || "".equals(hostText.getText())){
			updateStatus("Host should be specified for LDAP server!");
			return false;
		}else if (portText.getText() == null || "".equals(portText.getText())){
			updateStatus("Port should be specified for LDAP server!");
			return false;
		}else if (portText.getText() != null && !portText.getText().isEmpty()){
			try{
				Integer.parseInt(portText.getText());
			}catch(NumberFormatException e){
				updateStatus("Port value should be an Integer!");
				return false;
			}
		}else if (nameText.getText().getText() == null || "".equals(nameText.getText().getText())){
			updateStatus("Name should be specified for LDAP server!");
			return false;
		}
		updateStatus(null);
		return true;
	}
}
