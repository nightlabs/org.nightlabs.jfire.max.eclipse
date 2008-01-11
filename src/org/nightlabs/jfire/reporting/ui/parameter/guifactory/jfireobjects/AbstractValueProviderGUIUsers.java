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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
		searchComposite = new UserSearchComposite(wrapper, SWT.NONE, getTypeFlags() | UserSearchComposite.FLAG_MULTI_SELECTION | UserSearchComposite.FLAG_SEARCH_BUTTON);
		searchComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				notifyOutputChanged();
			}
		});		
		return searchComposite;
	}
	
	protected abstract int getTypeFlags();

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Collection<UserID> getOutputValue() {
		Collection<org.nightlabs.jfire.security.User> users = searchComposite.getSelectedUsers();
		Collection<UserID> result = new ArrayList<UserID>(users.size());
		for (User user : users) {
			result.add((UserID) JDOHelper.getObjectId(user));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return searchComposite.getSelectedUsers().size() > 0;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {		
	}

}
