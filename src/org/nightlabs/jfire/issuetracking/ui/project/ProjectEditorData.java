package org.nightlabs.jfire.issuetracking.ui.project;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import org.nightlabs.jfire.security.User;

public class ProjectEditorData{
	private Collection<User> users;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public ProjectEditorData() {}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		Collection<User> oldUsers = this.users;
        this.users = users;
		pcs.firePropertyChange("users", oldUsers, users);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
}