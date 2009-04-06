package org.nightlabs.jfire.entityuserset.ui;

import java.util.Set;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.entityuserset.EntityUserSetController;
import org.nightlabs.jfire.security.AuthorizedObject;
import org.nightlabs.jfire.security.UserSecurityGroup;
import org.nightlabs.jfire.security.dao.AuthorizedObjectDAO;
import org.nightlabs.jfire.security.id.AuthorizedObjectID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class EntityUserSetControllerClientImpl extends EntityUserSetController 
{
	private  static final String[] FETCH_GROUPS = new String[] {AuthorizedObject.FETCH_GROUP_NAME, AuthorizedObject.FETCH_GROUP_DESCRIPTION};
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.EntityUserSetController#getUserSecurityGroupMemberIDs(org.nightlabs.jfire.security.id.AuthorizedObjectID)
	 */
	@Override
	public Set<AuthorizedObjectID> getUserSecurityGroupMemberIDs(AuthorizedObjectID authorizedObjectID) 
	{
		AuthorizedObject authorizedObject = AuthorizedObjectDAO.sharedInstance().getAuthorizedObject(authorizedObjectID, FETCH_GROUPS, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		if (!(authorizedObject instanceof UserSecurityGroup))
			return null;

		UserSecurityGroup userSecurityGroup = (UserSecurityGroup) authorizedObject;
		Set<AuthorizedObject> members = userSecurityGroup.getMembers();
		return NLJDOHelper.getObjectIDSet(members);
	}

}
