package org.nightlabs.jfire.auth.ui.ldap.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Controller for {@link LDAPServerEditorMainPage}. Retrieves and stores {@link LDAPServer} instances.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 */
public class LDAPServerMainPageController extends ActiveEntityEditorPageController<LDAPServer>{

	private UserManagementSystemID ldapServerID;
	
	public LDAPServerMainPageController(EntityEditor editor){
		super(editor, true);
		this.ldapServerID = (UserManagementSystemID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	private static final String[] FETCH_GROUPS_LDAP_SERVER = {
		FetchPlan.DEFAULT,
		LDAPServer.FETCH_GROUP_NAME,
		LDAPServer.FETCH_GROUP_DESCRIPTION,
		LDAPServer.FETCH_GROUP_TYPE,
		UserManagementSystemType.FETCH_GROUP_NAME,
		UserManagementSystemType.FETCH_GROUP_DESCRIPTION
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LDAPServer retrieveEntity(ProgressMonitor monitor) {
		if (ldapServerID != null){
			
			UserManagementSystem userManagementSystem = UserManagementSystemDAO.sharedInstance().getUserManagementSystem(
					ldapServerID, FETCH_GROUPS_LDAP_SERVER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
					);
			
			if (!(userManagementSystem instanceof LDAPServer)){
				throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerMainPageController.wrongEditorInputExceptionText")); //$NON-NLS-1$
			}

			return (LDAPServer) userManagementSystem;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LDAPServer storeEntity(LDAPServer ldapServer, ProgressMonitor monitor) {
		return UserManagementSystemDAO.sharedInstance().storeUserManagementSystem(
				getControllerObject(), true, FETCH_GROUPS_LDAP_SERVER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_LDAP_SERVER;
	}
	
}
