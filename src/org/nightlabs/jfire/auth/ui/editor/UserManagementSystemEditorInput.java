package org.nightlabs.jfire.auth.ui.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;

/**
 * Editor input for {@link UserManagementSystem} editor.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 */
public class UserManagementSystemEditorInput extends JDOObjectEditorInput<UserManagementSystemID>{
	
	/**
	 * Class of specific {@link UserManagementSystemType} object which is needed for getting editor pages 
	 * according to concrete {@link UserManagementSystem} implementation.
	 */
	private Class<? extends UserManagementSystemType<?>> userManagementSystemClass;
	
	/**
	 * Constructor for an existing {@link UserManagementSystem}.
	 * @param userID The user
	 */
	public UserManagementSystemEditorInput(UserManagementSystemID userManagementSystemID, Class<? extends UserManagementSystemType<?>> userManagementSystemClass){
		super(userManagementSystemID);
		this.userManagementSystemClass = userManagementSystemClass;
		setName(Messages.getString("org.nightlabs.jfire.auth.ui.editor.UserManagementSystemEditorInput.editorInputName")); //$NON-NLS-1$
	}
	
	/**
	 * Get {@link UserManagementSystemType} class.
	 * 
	 * @return {@link Class} object of {@link UserManagementSystemType} of specific {@link UserManagementSystem} being edited 
	 */
	public Class<? extends UserManagementSystemType<?>> getUserManagementSystemTypeClass() {
		return userManagementSystemClass;
	}
	
}
