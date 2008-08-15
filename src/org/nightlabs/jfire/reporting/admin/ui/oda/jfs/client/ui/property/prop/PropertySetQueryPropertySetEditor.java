/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.prop;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.AbstractJFSQueryPropertySetEditor;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PropertySetQueryPropertySetEditor extends AbstractJFSQueryPropertySetEditor {

	private StructLocalTableComposite structLocalTableComposite;
	
	public PropertySetQueryPropertySetEditor() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		structLocalTableComposite = new StructLocalTableComposite(parent, SWT.NONE);
		if (getQueryPropertySet() != null) {
			structLocalTableComposite.setJFSQueryPropertySet(getQueryPropertySet());
		}
		return structLocalTableComposite;
	}
	
	@Override
	public void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet) {
		super.setJFSQueryPropertySet(queryPropertySet);
		if (structLocalTableComposite != null) {
			structLocalTableComposite.setJFSQueryPropertySet(queryPropertySet);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return structLocalTableComposite;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.IJFSQueryPropertySetEditor#getProperties()
	 */
	@Override
	public Map<String, String> getProperties() {
		return structLocalTableComposite.getProperties();
	}
}
