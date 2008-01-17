/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.security.User;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SelectedUsersTable extends AbstractTableComposite<User> {

	public class LabelProvider extends TableLabelProvider {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof User) {
				User user = (User) element;
				return String.format(
					"%s (%s)",
					user.getUserID(), user.getName(), user.getDescription()
				);
			}
			return "";
		}
	}
	
	private Collection<User> users = new ArrayList<User>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public SelectedUsersTable(Composite parent, int style) {
		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		// TODO: FIXME: Fix Column layout on windows (column will be rendered only as wide as its header-text)
//		TableLayout l = new TableLayout();
//		new TableColumn(table, SWT.LEFT).setText("Selected user");		
//		l.addColumnData(new ColumnWeightData(10));
//		table.setLayout(l);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setInput(users);
	}
	
	public Collection<User> getSelectedUsers() {
		return new ArrayList<User>(users);
	}
	
	public void addUser(User user) {
		if (!users.contains(user)) {
			users.add(user);
			setInput(users);
		}
	}
	
	public void removeUser(User user) {
		users.remove(user);
		setInput(users);
	}

	public void removeSelectedUsers() {
		Collection<User> sel = getSelectedElements();
		for (User user : sel) {
			users.remove(user);
		}
		setInput(users);
	}
}
