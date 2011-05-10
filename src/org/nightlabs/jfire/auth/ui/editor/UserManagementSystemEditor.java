package org.nightlabs.jfire.auth.ui.editor;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageSettings;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.auth.ui.UserManagementSystemUIMappingRegistry;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Editor for {@link UserManagementSystem} objects. It's registered to edit every {@link UserManagementSystem}, 
 * therefore we use org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping extension point for adding only
 * needed editor pages for currently edited {@link UserManagementSystem} (see overidden {@link #getPageSettingsOrdered()}).
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserManagementSystemEditor extends ActiveEntityEditor{
	
	/**
	 * Default constructor.
	 */
	public UserManagementSystemEditor() {
		super();
	}
	
	/**
	 * As {@link UserManagementSystemEditor} is used for every {@link UserManagementSystem} we override this method to filter editor pages
	 * which are suited for specific user management system being edited. We simply get {@link List} of all {@link EntityEditorPageSettings} 
	 * and then just remove elements from it based on page factory class name.
	 * {@link IllegalStateException} is thrown if it appears that no page factories are mapped to a specific {@link UserManagementSystem}. 
	 */
	@Override
	protected List<EntityEditorPageSettings> getPageSettingsOrdered() {
		
		Class<? extends UserManagementSystem> userManagementSystemClass = ((UserManagementSystemEditorInput) getEditorInput()).getUserManagementSystemClass();
		
		Set<String> pageFactoryClassNames = UserManagementSystemUIMappingRegistry.sharedInstance().getPageFactoryClassNames(userManagementSystemClass);
		if (pageFactoryClassNames == null || pageFactoryClassNames.isEmpty()){
			throw new IllegalStateException("There's no EntityEditor pages defined for object of type " + userManagementSystemClass.getName());
		}

		List<EntityEditorPageSettings> pageSettings = super.getPageSettingsOrdered();
		
		for (Iterator<EntityEditorPageSettings> iterator = pageSettings.iterator(); iterator.hasNext();) {
			EntityEditorPageSettings entityEditorPageSettings = (EntityEditorPageSettings) iterator.next();
			if (!pageFactoryClassNames.contains(entityEditorPageSettings.getPageFactory().getClass().getName())){
				iterator.remove();
			}
		} 
		
		return pageSettings;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		super.firePropertyChange(PROP_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		if (entity instanceof UserManagementSystem) {
			UserManagementSystem userManagementSystem = (UserManagementSystem) entity;
			String userManagementSystemName = userManagementSystem.getUserManagementSystemID()+User.SEPARATOR_BETWEEN_USER_ID_AND_ORGANISATION_ID+userManagementSystem.getOrganisationID();
			if (userManagementSystem.getName() != null 
					&& !"".equals(userManagementSystem.getName().getText())){
				userManagementSystemName = userManagementSystem.getName().getText();
			}
			String userManagementSystemTypeName = userManagementSystem.getType().getClass().getSimpleName();
			if (userManagementSystem.getType().getName() != null 
					&& !"".equals(userManagementSystem.getType().getName().getText())){
				userManagementSystemTypeName = userManagementSystem.getType().getName().getText();
			}
			return userManagementSystemName + " [" + userManagementSystemTypeName + "]";
		}
		return entity.getClass().getSimpleName();
	}

	private static final String[] FETCH_GROUPS_USER_MANAGEMENT_SYSTEM = {
		UserManagementSystem.FETCH_GROUP_NAME,
		UserManagementSystem.FETCH_GROUP_TYPE,
		UserManagementSystemType.FETCH_GROUP_NAME,
		FetchPlan.DEFAULT
		};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		return UserManagementSystemDAO.sharedInstance().getUserManagementSystem(
				((UserManagementSystemEditorInput)getEditorInput()).getJDOObjectID(), 
				FETCH_GROUPS_USER_MANAGEMENT_SYSTEM, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
				);
	}

}
