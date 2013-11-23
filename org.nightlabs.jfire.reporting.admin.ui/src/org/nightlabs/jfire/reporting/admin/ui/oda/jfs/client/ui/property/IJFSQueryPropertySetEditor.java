/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetWizardPage;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;

/**
 * {@link IJFSQueryPropertySetEditor}s are used to create a GUI that helps the user
 * to create the {@link JFSQueryPropertySet} of a data-source script.
 * They are utilized by {@link JFSQueryPropertySetWizardPage}.
 * <p>
 * {@link IJFSQueryPropertySetEditorFactory}s create instances of {@link IJFSQueryPropertySetEditor}.
 * The factories are registered as extension (see {@link IJFSQueryPropertySetEditorFactory}).
 * </p> 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IJFSQueryPropertySetEditor {
	
	/**
	 * Create the {@link Control} of this editor, that helps the user
	 * to build/edit the {@link JFSQueryPropertySet} passed to this editor.
	 * 
	 * @param parent The parent for the new {@link Control}-
	 * @return A new {@link Control}.
	 */
	Control createControl(Composite parent);
	/**
	 * Set the {@link JFSQueryPropertySet} that this editor should edit.
	 * <p>
	 * Note, that this will be called after {@link #createControl(Composite)}
	 * and therefore should apply the values directly to the GUI.
	 * </p>
	 * @param queryPropertySet The {@link JFSQueryPropertySet} to set.
	 */
	void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet);
	/**
	 * Return the {@link Control} created in {@link #createControl(Composite)}.
	 * 
	 * @return The {@link Control} created in {@link #createControl(Composite)}.
	 */
	Control getControl();
	/**
	 * Collect the properties from the GUI that should be set to the {@link JFSQueryPropertySet}.
	 * 
	 * @return The properties collected from the GUI.
	 */
	Map<String, String> getProperties();
}
