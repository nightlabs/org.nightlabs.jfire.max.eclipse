/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.id.UserID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractValueProviderGUIUsers extends AbstractValueProviderGUI<Collection<UserID>> {
	
	private UserSearchComposite searchComposite;
	private SelectedUsersTable selectedUsersTable;
	

	/**
	 * 
	 */
	public AbstractValueProviderGUIUsers(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		Group group = new Group(wrapper, SWT.NONE);
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.ORDINARY_WRAPPER, gl);
		group.setLayout(gl);		
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText(getValueProviderConfig().getMessage().getText());
		
		gl.numColumns = 3;
		gl.makeColumnsEqualWidth = false;
		
		searchComposite = new UserSearchComposite(group, SWT.NONE, getTypeFlags() | UserSearchComposite.FLAG_MULTI_SELECTION | UserSearchComposite.FLAG_SEARCH_BUTTON);
		searchComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				notifyOutputChanged();
			}
		});		
		
		XComposite buttonComp = new XComposite(group, SWT.NONE);
		buttonComp.getGridData().grabExcessHorizontalSpace = false;
		
		Button add = new Button(buttonComp, SWT.PUSH);
		add.setText(">>");
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<User> users = searchComposite.getSelectedUsers();
				for (User user : users) {
					selectedUsersTable.addUser(user);
					notifyOutputChanged();
				}
			}
		});
		
		Button remove = new Button(buttonComp, SWT.PUSH);
		remove.setText("<<");
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedUsersTable.removeSelectedUsers();
				notifyOutputChanged();
			}
		});
		
		XComposite selectionWrapper = new XComposite(group, SWT.NONE, LayoutMode.TIGHT_WRAPPER);		
		Label label = new Label(selectionWrapper, SWT.WRAP);
		label.setText("Selected objects");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectedUsersTable = new SelectedUsersTable(selectionWrapper, SWT.NONE);
		
		return group;
	}
	
	protected abstract int getTypeFlags();

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Collection<UserID> getOutputValue() {
		Collection<User> users;		
		Collection<User> selectedUsers = selectedUsersTable.getSelectedUsers();
		if (selectedUsers.size() == 0) {
			users = searchComposite.getSelectedUsers();
		} else {
			users = selectedUsers;
		}
		Collection<UserID> result = new ArrayList<UserID>(users.size());
		for (User user : users) {
			result.add((UserID) JDOHelper.getObjectId(user));
		}
		if (users.size() == 0)
			return null;
		return result;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return getOutputValue() != null || getValueProviderConfig().isAllowNullOutputValue();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {		
	}

}
