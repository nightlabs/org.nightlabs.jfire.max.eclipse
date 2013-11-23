package org.nightlabs.jfire.auth.ui.ldap.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSetDAO;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Controller for {@link LDAPServerEditorScriptSetPage}. Retrieves and stores {@link LDAPScriptSet} instances.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 */
public class LDAPServerScriptSetPageController extends ActiveEntityEditorPageController<LDAPScriptSet>{

	private UserManagementSystemID ldapServerID;
	
	public LDAPServerScriptSetPageController(EntityEditor editor){
		super(editor, false);
		this.ldapServerID = (UserManagementSystemID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	private static final String[] FETCH_GROUPS_LDAP_SCRIPT_SET = {
		FetchPlan.DEFAULT
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LDAPScriptSet retrieveEntity(ProgressMonitor monitor) {
		if (ldapServerID != null){
			
			return LDAPScriptSetDAO.sharedInstance().getLDAPScriptSetByLDAPServerID(
					ldapServerID, FETCH_GROUPS_LDAP_SCRIPT_SET, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
					);
			
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LDAPScriptSet storeEntity(LDAPScriptSet ldapServer, ProgressMonitor monitor) {
		return LDAPScriptSetDAO.sharedInstance().storeLDAPScriptSet(
				getControllerObject(), true, FETCH_GROUPS_LDAP_SCRIPT_SET, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_LDAP_SCRIPT_SET;
	}
	
}
