/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;

/**
 * This {@link Composite} shows a {@link JFSQueryPropertySetTable} to edit the 
 * properties of a given {@link JFSQueryPropertySet} as well as 'Add' and 'Remove'
 * buttons to the right of the table for manipulating the list.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DefaultJFSQueryPropertySetEditorComposite extends XComposite {

	private JFSQueryPropertySetTable propertySetTable;
	private Button add;
	private Button remove;
	
	/**
	 * Create a new {@link DefaultJFSQueryPropertySetEditor} for the given parent using the given style.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to apply.
	 */
	public DefaultJFSQueryPropertySetEditorComposite(Composite parent, int style) {
		super(parent, style);
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		propertySetTable = new JFSQueryPropertySetTable(this, SWT.NONE);		
		XComposite buttonWrapper = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER);
		buttonWrapper.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		add = new Button(buttonWrapper, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.setText("Add");
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propertySetTable.createNewEntry();
			}
		});
		remove = new Button(buttonWrapper, SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.setText("Remove");
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propertySetTable.removeFirstSelectedEntry();
			}
		});
	}
	
	/**
	 * Initialize this Composite with the properties of the given {@link JFSQueryPropertySet}.
	 * 
	 * @param queryPropertySet The {@link JFSQueryPropertySet} to initialize with.
	 */
	public void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet) {
		propertySetTable.setJFSQueryPropertySet(queryPropertySet);
	}
	
	/**
	 * Get the table showing the current properties.
	 * @return The table showing the current properties. 
	 */
	public JFSQueryPropertySetTable getPropertySetTable() {
		return propertySetTable;
	}
	
}
