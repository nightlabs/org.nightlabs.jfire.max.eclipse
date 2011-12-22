package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.jfire.auth.ui.editor.UserSecurityGroupSyncConfigSpecificComposite;
import org.nightlabs.jfire.auth.ui.ldap.LDAPEntrySelectorComposite;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPUserSecurityGroupSyncConfig;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
import org.nightlabs.util.CollectionUtil;

/**
 * LDAP implementation of {@link UserSecurityGroupSyncConfigSpecificComposite}. 
 * Gives a possibility to edit {@link LDAPUserSecurityGroupSyncConfig#ldapGroupName} field and shows some useful info
 * on {@link LDAPServer} being selected.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerUserSecurityGroupSyncConfigComposite extends UserSecurityGroupSyncConfigSpecificComposite{

	private LDAPServerUserSecurityGroupSyncConfigModel model;

	private LDAPEntrySelectorComposite ldapGroupNameSelector;

	private FormText ldapServerInfoFormText;

	/**
	 * Constructs a new {@link LDAPServerUserSecurityGroupSyncConfigComposite}
	 * 
	 * @param parent Parent {@link Composite}
	 * @param style {@link SWT} style
	 * @param toolkit {@link FormToolkit} to be used for creation of composite's content
	 */
	public LDAPServerUserSecurityGroupSyncConfigComposite(Composite parent, int style, FormToolkit toolkit){
		super(parent, style, toolkit);
		GridLayout parentLayout = new GridLayout(1, false); 
		parentLayout.verticalSpacing = 10;
		parentLayout.marginTop = 10;
		this.setLayout(parentLayout);
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		createContents(toolkit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commitChanges() {
		if (model != null){
			model.setLDAPGroupName(ldapGroupNameSelector.getEntryName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		if (!(getSyncConfig() instanceof LDAPUserSecurityGroupSyncConfig)){
			throw new IllegalArgumentException(
					"Input should be an instance of LDAPUserSecurityGroupSyncConfig! Instead it is " + getSyncConfig()!=null ? getSyncConfig().getClass().getName() : null);
		}
		this.model = new LDAPServerUserSecurityGroupSyncConfigModel((LDAPUserSecurityGroupSyncConfig) getSyncConfig());
		
		ldapGroupNameSelector.setEntryName(model.getLDAPGroupName());
		ldapGroupNameSelector.setLdapConnectionParamsProvider(model.getLdapServer());
		setBindCredentialsToSelector(ldapGroupNameSelector);
		
		StringBuilder ldapInfoStringBuilder = new StringBuilder();
		LDAPServer ldapServer = model.getLdapServer();
		ldapInfoStringBuilder.append("<form><p><b>LDAPServer info</b></p>");
		ldapInfoStringBuilder.append("<p><b>Location:</b> ").append(ldapServer.getHost()).append(":").append(ldapServer.getPort()).append("</p>");
		ldapInfoStringBuilder.append("<p><b>Is active:</b> ").append(ldapServer.isActive() ? "yes" : "no").append("</p>");
		ldapInfoStringBuilder.append("<p><b>Is leading:</b> ").append(ldapServer.isLeading() ? "yes" : "no").append("</p>");
		ldapInfoStringBuilder.append("<p><b>Encryption method:</b> ").append(ldapServer.getEncryptionMethod().stringValue()).append("</p>");
		ldapInfoStringBuilder.append("<p><b>Authentication method:</b> ").append(ldapServer.getAuthenticationMethod().stringValue()).append("</p>");
		ldapInfoStringBuilder.append("</form>");
		ldapServerInfoFormText.setText(ldapInfoStringBuilder.toString(), true, false);
	}

	private void createContents(FormToolkit toolkit){
		
		Composite wrapper = toolkit.createComposite(this, SWT.NONE);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 50;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		wrapper.setLayout(gLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		wrapper.setLayoutData(gd);
		
		LDAPAttributeSet selectionCriteria = new LDAPAttributeSet();
		selectionCriteria.createAttribute(
				"objectClass", CollectionUtil.createHashSet(LDAPServer.GROUP_OF_NAMES_ATTR_VALUE, LDAPServer.GROUP_OF_UNIQUE_NAMES_ATTR_VALUE)); //$NON-NLS-1$
		ldapGroupNameSelector = new LDAPEntrySelectorComposite(wrapper, SWT.NONE, selectionCriteria);
		ldapGroupNameSelector.setToolTipText("Open");
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 400;
		gd.minimumWidth = 400;
		ldapGroupNameSelector.setLayoutData(gd);
		ldapGroupNameSelector.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				if (!ldapGroupNameSelector.getEntryName().equals(getSyncConfig().getUserManagementSystemSecurityObject())){
					fireSyncConfigChanged();
				}
			}
		});
		
		ldapServerInfoFormText = toolkit.createFormText(wrapper, false);
		ldapServerInfoFormText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		ldapServerInfoFormText.setText("<form><p><b>LDAPServer info: </b>no server selected!</p></form>", true, false);

	}

	private boolean credentialsSet = false;
	private void setBindCredentialsToSelector(LDAPEntrySelectorComposite selectorComposite){
		if (credentialsSet){
			return;
		}
		LDAPEntrySelectorComposite.setBindCredentialsToSelector(selectorComposite, model.getLdapServer());
		credentialsSet = true;
	}

}
