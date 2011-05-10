package org.nightlabs.jfire.auth.ui.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;

/**
 * Editor input for {@link UserManagementSystem} editor.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 */
public class UserManagementSystemEditorInput extends JDOObjectEditorInput<UserManagementSystemID>{
	
	/**
	 * Class of specific {@link UserManagementSystem} object which is needed for getting editor pages 
	 * according to concrete {@link UserManagementSystem} implementation.
	 */
	private Class<? extends UserManagementSystem> userManagementSystemClass;
	
	/**
	 * Constructor for an existing {@link UserManagementSystem}.
	 * @param userID The user
	 */
	public UserManagementSystemEditorInput(UserManagementSystemID userManagementSystemID, Class<? extends UserManagementSystem> userManagementSystemClass){
		super(userManagementSystemID);
		this.userManagementSystemClass = userManagementSystemClass;
		setName("UserManagementSystem input"); //$NON-NLS-1$
	}
	
	/**
	 * Get {@link UserManagementSystem} class.
	 * 
	 * @return {@link Class} object of specific {@link UserManagementSystem} being edited 
	 */
	public Class<? extends UserManagementSystem> getUserManagementSystemClass() {
		return userManagementSystemClass;
	}
	
}
