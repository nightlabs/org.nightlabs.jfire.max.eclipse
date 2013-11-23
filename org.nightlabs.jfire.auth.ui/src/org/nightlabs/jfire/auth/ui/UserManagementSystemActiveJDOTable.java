package org.nightlabs.jfire.auth.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Active JDO Table for representing {@link UserManagementSystem}s showing their names, types and leading status.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserManagementSystemActiveJDOTable extends ActiveJDOObjectTableComposite<UserManagementSystemID, UserManagementSystem>{

	/**
	 * The fetch groups of {@link UserManagementSystem} data.
	 */
	public static final String[] USER_MANAGEMENT_SYSTEM_FETCH_GROUPS = new String[]{
		FetchPlan.DEFAULT,
		UserManagementSystem.FETCH_GROUP_NAME,
		UserManagementSystem.FETCH_GROUP_TYPE,
		UserManagementSystemType.FETCH_GROUP_NAME
		};

	private class UserManagementSystemController extends ActiveJDOObjectController<UserManagementSystemID, UserManagementSystem> {
		@Override
		protected Class<? extends UserManagementSystem> getJDOObjectClass() {
			return UserManagementSystem.class;
		}

		@Override
		protected Collection<UserManagementSystem> retrieveJDOObjects(Set<UserManagementSystemID> objectIDs, ProgressMonitor monitor) {
			return UserManagementSystemDAO.sharedInstance().getUserManagementSystems(
					objectIDs, USER_MANAGEMENT_SYSTEM_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		}

		@Override
		protected Collection<UserManagementSystem> retrieveJDOObjects(ProgressMonitor monitor) {
			return UserManagementSystemDAO.sharedInstance().getAllUserManagementSystems(
					USER_MANAGEMENT_SYSTEM_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		}

		@Override
		protected void sortJDOObjects(List<UserManagementSystem> objects) {
			Collections.sort(objects);
		}
	}


	/**
	 * {@inheritDoc} 
	 */
	public UserManagementSystemActiveJDOTable(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.UserManagementSystemTable.columnName")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.UserManagementSystemTable.columnType")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.auth.ui.UserManagementSystemTable.columnLeading")); //$NON-NLS-1$
		
		table.setLayout(new WeightedTableLayout(new int[] {40, 40, 20}));
	}

	@Override
	protected ActiveJDOObjectController<UserManagementSystemID, UserManagementSystem> createActiveJDOObjectController() {
		return new UserManagementSystemController();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new UserManagementSystemTableLabelProvider();
	}

	class UserManagementSystemTableLabelProvider extends TableLabelProvider	{
		
		public UserManagementSystemTableLabelProvider() {
			super();
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof String){
				return (String) element;
			}else if (element instanceof UserManagementSystem) {
				UserManagementSystem userManagementSystem = (UserManagementSystem) element;
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
						return userManagementSystem.isLeading() ? Messages.getString("org.nightlabs.jfire.auth.ui.UserManagementSystemTable.leadingLabel_yes") : Messages.getString("org.nightlabs.jfire.auth.ui.UserManagementSystemTable.leadingLabel_no"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			return null;
		}
	}

}
