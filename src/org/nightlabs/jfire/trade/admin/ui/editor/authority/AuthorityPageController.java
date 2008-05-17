package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.admin.ui.editor.user.RoleGroupSecurityPreferencesModel;
import org.nightlabs.jfire.security.Authority;
import org.nightlabs.jfire.security.AuthorityType;
import org.nightlabs.jfire.security.RoleGroup;
import org.nightlabs.jfire.security.RoleGroupRef;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.UserRef;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;

public class AuthorityPageController
extends AbstractProductTypePageController<ProductType>
{

	public AuthorityPageController(EntityEditor editor) {
		super(editor);
	}

	@Override
	public ProductType getExtendedProductType(ProgressMonitor monitor, ProductTypeID extendedProductTypeID) {
		return ProductTypeDAO.sharedInstance().getProductType(extendedProductTypeID,
				FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY;
	}

	private static final String[] FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		ProductTypeLocal.FETCH_GROUP_SECURING_AUTHORITY,
		AuthorityType.FETCH_GROUP_ROLE_GROUPS,
		RoleGroup.FETCH_GROUP_NAME,
		RoleGroup.FETCH_GROUP_DESCRIPTION,
		Authority.FETCH_GROUP_NAME,
		Authority.FETCH_GROUP_DESCRIPTION,
		Authority.FETCH_GROUP_USER_REFS,
		UserRef.FETCH_GROUP_USER,
		UserRef.FETCH_GROUP_ROLE_GROUP_REFS,
		RoleGroupRef.FETCH_GROUP_ROLE_GROUP,
	};

	@Override
	protected ProductType retrieveEntity(ProgressMonitor monitor) {
		return ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(),
				FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	public AuthorityType getAuthorityType()
	{
		return getControllerObject() == null ? null : getControllerObject().getProductTypeLocal().getSecuringAuthorityType();
	}

	public Authority getAuthority()
	{
		return getControllerObject() == null ? null : getControllerObject().getProductTypeLocal().getSecuringAuthority();
	}

	private Set<RoleGroup> roleGroupsInAuthorityType = new HashSet<RoleGroup>();

	private Set<User> usersInAuthority = new HashSet<User>();
	private Set<User> usersToAdd = new HashSet<User>();
	private Set<User> usersToRemove = new HashSet<User>();

	public Set<User> getUsersInAuthority() {
		return usersInAuthority;
	}
	public Set<User> getUsersToAdd() {
		return usersToAdd;
	}
	public Set<User> getUsersToRemove() {
		return usersToRemove;
	}

	private Map<User, RoleGroupSecurityPreferencesModel> user2roleGroupSecurityPreferencesModel = new HashMap<User, RoleGroupSecurityPreferencesModel>();

	@Override
	protected void fireModifyEvent(Object oldObject, Object newObject, boolean resetDirtyState) {
		// whenever things changed, we first get our preprocessed data right
		AuthorityType authorityType = getAuthorityType();
		if (authorityType == null)
			roleGroupsInAuthorityType = Collections.emptySet();
		else
			roleGroupsInAuthorityType = authorityType.getRoleGroups();

		Authority authority = getAuthority();
		if (authority == null) {
			usersInAuthority = new HashSet<User>();
			usersToAdd = new HashSet<User>();
			usersToRemove = new HashSet<User>();
			user2roleGroupSecurityPreferencesModel = new HashMap<User, RoleGroupSecurityPreferencesModel>();
		}
		else {
			Set<User> usersInAuthority = new HashSet<User>();
			Map<User, RoleGroupSecurityPreferencesModel> user2roleGroupSecurityPreferencesModel = new HashMap<User, RoleGroupSecurityPreferencesModel>();


			for (UserRef userRef : authority.getUserRefs()) {
				usersInAuthority.add(userRef.getUser());
				RoleGroupSecurityPreferencesModel roleGroupSecurityPreferencesModel = new RoleGroupSecurityPreferencesModel();
				user2roleGroupSecurityPreferencesModel.put(userRef.getUser(), roleGroupSecurityPreferencesModel);
				Set<RoleGroup> roleGroupsOfUser = new HashSet<RoleGroup>();
				for (RoleGroupRef roleGroupRef : userRef.getRoleGroupRefs())
					roleGroupsOfUser.add(roleGroupRef.getRoleGroup());

				roleGroupSecurityPreferencesModel.setAvailableRoleGroups(roleGroupsInAuthorityType);
				roleGroupSecurityPreferencesModel.setRoleGroups(roleGroupsOfUser);
				// TODO we need the groups from the userGroups - maybe obtain all this data from the server directly instead of putting it together here?!
			}

			this.usersInAuthority = usersInAuthority;
			this.usersToAdd = new HashSet<User>();
			this.usersToRemove = new HashSet<User>();
			this.user2roleGroupSecurityPreferencesModel = user2roleGroupSecurityPreferencesModel;
		}

		// and now with correct data, we continue notifying
		super.fireModifyEvent(oldObject, newObject, resetDirtyState);
	}
	

	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		// TODO implement storing
		return null;
	}

}
