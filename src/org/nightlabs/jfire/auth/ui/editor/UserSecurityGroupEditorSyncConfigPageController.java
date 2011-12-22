package org.nightlabs.jfire.auth.ui.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.security.dao.UserSecurityGroupDAO;
import org.nightlabs.jfire.security.dao.UserSecurityGroupSyncConfigContainerDAO;
import org.nightlabs.jfire.security.id.UserSecurityGroupID;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfigContainer;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * Controller for {@link UserSecurityGroupEditorSyncConfigPage}. Retrieves and stores a {@link UserSecurityGroupSyncConfigContainer}.
 * If there's no {@link UserSecurityGroupSyncConfigContainer} for given {@link #userSecurityGroupID} it creates a new one here in
 * {@link #retrieveEntity(ProgressMonitor)}. This controller also performs a check that {@link UserSecurityGroupSyncConfigContainer}
 * being stored contains at least one {@link UserSecurityGroupSyncConfig}, otherwise this container is not stored on server not to 
 * pollute datastore with such empty objects that have no value for the system.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 */
public class UserSecurityGroupEditorSyncConfigPageController extends ActiveEntityEditorPageController<UserSecurityGroupSyncConfigContainer>{

	private UserSecurityGroupID userSecurityGroupID;
	
	public UserSecurityGroupEditorSyncConfigPageController(EntityEditor editor){
		super(editor, true);
		this.userSecurityGroupID = (UserSecurityGroupID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	private static final String[] FETCH_GROUPS_SYNC_CONFIG_CONTAINER = {
		FetchPlan.DEFAULT,
		UserSecurityGroupSyncConfigContainer.FETCH_GROUP_USER_SECURITY_GROUP,
		UserSecurityGroupSyncConfigContainer.FETCH_GROUP_USER_SECURITY_GROUP_SYNC_CONFIGS,
		UserSecurityGroupSyncConfig.FETCH_GROUP_USER_MANAGEMENT_SYSTEM,
		UserManagementSystem.FETCH_GROUP_NAME,
		UserManagementSystem.FETCH_GROUP_TYPE
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserSecurityGroupSyncConfigContainer retrieveEntity(ProgressMonitor monitor) {
		if (userSecurityGroupID != null){
			try{
				monitor.beginTask("Loading user security groups sync config container...", 2);
				UserSecurityGroupSyncConfigContainer container = UserSecurityGroupSyncConfigContainerDAO.sharedInstance().getSyncConfigsContainerByGroupID(
						userSecurityGroupID, FETCH_GROUPS_SYNC_CONFIG_CONTAINER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));
				monitor.worked(1);
				if (container == null){
					container = new UserSecurityGroupSyncConfigContainer(
							UserSecurityGroupDAO.sharedInstance().getUserSecurityGroup(
									userSecurityGroupID, new String[]{FetchPlan.DEFAULT}, 1, new SubProgressMonitor(monitor, 1)));
				}
				monitor.worked(1);
				return container;
			}finally{
				monitor.done();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserSecurityGroupSyncConfigContainer storeEntity(UserSecurityGroupSyncConfigContainer syncConfigs, ProgressMonitor monitor) {
		UserSecurityGroupSyncConfigContainer container = getControllerObject();
		if (!container.getSyncConfigs().isEmpty()){
			return UserSecurityGroupSyncConfigContainerDAO.sharedInstance().storeSyncConfigContainer(
					container, true, FETCH_GROUPS_SYNC_CONFIG_CONTAINER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		}else{
			return container;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_SYNC_CONFIG_CONTAINER;
	}
	
}
