package org.nightlabs.jfire.auth.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.tree.EntityTree;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Entity tree category for {@link UserManagementSystem}s.
 *
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 */
public class EntityTreeCategoryUserManagementSystem extends ActiveJDOEntityTreeCategory<UserManagementSystemID, UserManagementSystem>{
	
	/**
	 * Label provider for {@link UserManagementSystem} objects in {@link EntityTree}.
	 * Returns name of {@link UserManagementSystem} as "name [type]". 
	 * If name and/or type are null or empty they are replaced with corresponding simple class names.
	 * 
	 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
	 */
	protected class UserManagementSystemLabelProvider extends TableLabelProvider {

		public String getColumnText(Object o, int columnIndex) {
			// check for string first, so we don't need to be logged in when displaying a simple string
			if (o instanceof String) {
				return (String) o;
			} else if (o instanceof UserManagementSystem) {
				UserManagementSystem userManagementSystem = (UserManagementSystem) o;
				String userManagementSystemName = userManagementSystem.getUserManagementSystemID()+User.SEPARATOR_BETWEEN_USER_ID_AND_ORGANISATION_ID+userManagementSystem.getOrganisationID();
				if (userManagementSystem.getName() != null 
						&& !"".equals(userManagementSystem.getName().getText())){ //$NON-NLS-1$
					userManagementSystemName = userManagementSystem.getName().getText();
				}
				String userManagementSystemTypeName = userManagementSystem.getType().getClass().getSimpleName();
				if (userManagementSystem.getType().getName() != null 
						&& !"".equals(userManagementSystem.getType().getName().getText())){ //$NON-NLS-1$
					userManagementSystemTypeName = userManagementSystem.getType().getName().getText();
				}
				return userManagementSystemName + " [" + userManagementSystemTypeName + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return super.getText(o);
			}
		}
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof UserManagementSystem){
				UserManagementSystem userManagementSystem = (UserManagementSystem) element;
				String imageSuffix = userManagementSystem.isActive()?"UserManagementSystemActive":"UserManagementSystemInactive"; //$NON-NLS-1$ //$NON-NLS-2$
				return SharedImages.getSharedImage(
						JFireAuthUIPlugin.sharedInstance(), EntityTreeCategoryUserManagementSystem.class, imageSuffix, ImageDimension._16x16, ImageFormat.png);
			}else{
				return super.getColumnImage(element, columnIndex);
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IEditorInput createEditorInput(Object o){
		UserManagementSystem userManagementSystem = (UserManagementSystem) o;
		UserManagementSystemID userManagementSystemID = UserManagementSystemID.create(userManagementSystem.getOrganisationID(), userManagementSystem.getUserManagementSystemID());
		return new UserManagementSystemEditorInput(userManagementSystemID, (Class<? extends UserManagementSystemType<?>>) userManagementSystem.getType().getClass());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITableLabelProvider createLabelProvider() {
		return new UserManagementSystemLabelProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<UserManagementSystem> getJDOObjectClass(){
		return UserManagementSystem.class;
	}

	private static final String[] FETCH_GROUPS_USER_MANAGEMENT_SYSTEM = {
		UserManagementSystem.FETCH_GROUP_NAME,
		UserManagementSystem.FETCH_GROUP_TYPE,
		UserManagementSystemType.FETCH_GROUP_NAME,
		FetchPlan.DEFAULT
		};

	// UserManagementSystem -> UserManagementSystemType -> UserManagementSystemTypeName -> names
	private static final int FETCH_DEPTH_USER_MANAGEMENT_SYSTEM = 4;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<UserManagementSystem> retrieveJDOObjects(Set<UserManagementSystemID> userIDs, ProgressMonitor monitor){
		return UserManagementSystemDAO.sharedInstance().getUserManagementSystems(
				userIDs, FETCH_GROUPS_USER_MANAGEMENT_SYSTEM, FETCH_DEPTH_USER_MANAGEMENT_SYSTEM, monitor
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<UserManagementSystem> retrieveJDOObjects(ProgressMonitor monitor){
		List<UserManagementSystem> userManagementSystems = UserManagementSystemDAO.sharedInstance().getAllUserManagementSystems(
				FETCH_GROUPS_USER_MANAGEMENT_SYSTEM, FETCH_DEPTH_USER_MANAGEMENT_SYSTEM, monitor
				);
		List<UserManagementSystem> res = new ArrayList<UserManagementSystem>(userManagementSystems.size());
		for (UserManagementSystem userManagementSystem : userManagementSystems) {
			res.add(userManagementSystem);
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void sortJDOObjects(List<UserManagementSystem> userManagementSystems){
		Collections.sort(userManagementSystems);	// note that UserManagementSystem implements Comparable
	}
	
}
