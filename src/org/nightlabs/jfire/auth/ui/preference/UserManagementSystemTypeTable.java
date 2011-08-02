package org.nightlabs.jfire.auth.ui.preference;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Table for representing existent {@link UserManagementSystemType}s.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserManagementSystemTypeTable extends AbstractTableComposite<UserManagementSystemType<?>>{

	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemTypeTable(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemTypeTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemTypeTable(Composite parent, int style, boolean initTable, int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.preference.UserManagementSystemTypeTable.columnName")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.preference.UserManagementSystemTypeTable.columnClass")); //$NON-NLS-1$
		
		table.setLayout(new WeightedTableLayout(new int[] {50, 50}));
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new UserManagementSystemTypeTableLabelProvider());
	}

	class UserManagementSystemTypeTableLabelProvider extends TableLabelProvider	{
		
		public UserManagementSystemTypeTableLabelProvider() {
			super();
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof String){
				return (String) element;
			}else if (element instanceof UserManagementSystemType) {
				UserManagementSystemType<?> userManagementSystemType = (UserManagementSystemType<?>) element;
				switch (columnIndex) {
					case(0):
						String name = userManagementSystemType.getClass().getSimpleName();
						if (userManagementSystemType.getName() != null
								&& userManagementSystemType.getName().getText() != null
								&& !userManagementSystemType.getName().getText().isEmpty()){
							name = userManagementSystemType.getName().getText(); 
						}
						return name;
					case(1):
						return userManagementSystemType.getClass().getSimpleName();
				}
			}
			return null;
		}
	}

}
