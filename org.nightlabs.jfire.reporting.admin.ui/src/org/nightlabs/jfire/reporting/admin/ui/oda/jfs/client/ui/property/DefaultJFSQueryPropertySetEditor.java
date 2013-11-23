/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;

/**
 * The default {@link IJFSQueryPropertySetEditor} that shows a composite with a 
 * table where the properties of a {@link JFSQueryPropertySet} can be directly
 * edited by the user.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DefaultJFSQueryPropertySetEditor extends AbstractJFSQueryPropertySetEditor {

	private DefaultJFSQueryPropertySetEditorComposite editorComposite;
	private JFSQueryPropertySet queryPropertySet;
	
	public DefaultJFSQueryPropertySetEditor() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#createControl(Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		editorComposite = new DefaultJFSQueryPropertySetEditorComposite(parent, SWT.NONE);
		if (queryPropertySet != null) {
			editorComposite.setJFSQueryPropertySet(queryPropertySet);
		}
		return editorComposite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.AbstractJFSQueryPropertySetEditor#setJFSQueryPropertySet(org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet)
	 */
	@Override
	public void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet) {
		super.setJFSQueryPropertySet(queryPropertySet);
		if (editorComposite != null && !editorComposite.isDisposed()) {
			editorComposite.setJFSQueryPropertySet(queryPropertySet);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return editorComposite;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#getProperties()
	 */
	@Override
	public Map<String, String> getProperties() {
		return editorComposite.getPropertySetTable().getProperties();
	}
}
