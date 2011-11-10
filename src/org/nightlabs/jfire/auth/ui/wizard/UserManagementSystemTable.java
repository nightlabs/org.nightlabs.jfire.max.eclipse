package org.nightlabs.jfire.auth.ui.wizard;

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
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Table for representing existent {@link UserManagementSystem}s.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserManagementSystemTable extends AbstractTableComposite<UserManagementSystem<?>>{

	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemTable(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemTable(Composite parent, int style, boolean initTable, int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.UserManagementSystemTable.columnName")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.UserManagementSystemTable.columnType")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.UserManagementSystemTable.columnLeading")); //$NON-NLS-1$
		
		table.setLayout(new WeightedTableLayout(new int[] {40, 40, 20}));
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new UserManagementSystemTableLabelProvider());
	}

	class UserManagementSystemTableLabelProvider extends TableLabelProvider	{
		
		public UserManagementSystemTableLabelProvider() {
			super();
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof String){
				return (String) element;
			}else if (element instanceof UserManagementSystem) {
				UserManagementSystem<?> userManagementSystem = (UserManagementSystem<?>) element;
				switch (columnIndex) {
					case(0):
						String name = userManagementSystem.getClass().getSimpleName();
						if (userManagementSystem.getName() != null
								&& userManagementSystem.getName().getText() != null
								&& !userManagementSystem.getName().getText().isEmpty()){
							name = userManagementSystem.getName().getText(); 
						}
						return name;
					case(1):
						UserManagementSystemType<?> type = userManagementSystem.getType();
						String typeName = type.getClass().getSimpleName();
						if (type.getName() != null
								&& type.getName().getText() != null
								&& !type.getName().getText().isEmpty()){
							typeName = type.getName().getText(); 
						}
						return typeName;
					case(2):
						return userManagementSystem.isLeading() ? Messages.getString("org.nightlabs.jfire.auth.ui.wizard.UserManagementSystemTable.leadingLabel_yes") : Messages.getString("org.nightlabs.jfire.auth.ui.wizard.UserManagementSystemTable.leadingLabel_no"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			return null;
		}
	}

}
