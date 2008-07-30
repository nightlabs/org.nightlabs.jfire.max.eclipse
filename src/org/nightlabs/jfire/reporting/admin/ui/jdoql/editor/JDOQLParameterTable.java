/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.jdoql.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class JDOQLParameterTable extends AbstractTableComposite<JDOQLParameterEntry> {

	protected class EditEntryDialog extends ResizableTitleAreaDialog {

		private Text name;
		private Text jScript;
		private JDOQLParameterEntry entry;
		
		public EditEntryDialog(Shell parentShell, JDOQLParameterEntry entry) {
			super(parentShell, null);
			this.entry = entry;
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			XComposite wrapper = new XComposite(parent, SWT.NONE);
			name = new Text(wrapper, SWT.BORDER);
			name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			jScript = new Text(wrapper, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			jScript.setLayoutData(new GridData(GridData.FILL_BOTH));
			if (entry != null) {
				name.setText(entry.getName() != null ? entry.getName() : "");
				jScript.setText(entry.getJScript() != null ? entry.getJScript() : "");
			}
			return super.createDialogArea(parent);
		}
		
		@Override
		protected void okPressed() {
			if (entry == null) {
				entry = new JDOQLParameterEntry();
			}
			entry.setName(name.getText());
			entry.setJScript(jScript.getText());
			super.okPressed();
		}
		
		public JDOQLParameterEntry getEntry() {
			return entry;
		}
	}
	
	protected class LabelProvider extends TableLabelProvider {

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0)
				return ((JDOQLParameterEntry) element).getName();
			if (columnIndex == 1)
				return ((JDOQLParameterEntry) element).getJScript();
			return "";
		}
	}
	
	private List<JDOQLParameterEntry> entries = new ArrayList<JDOQLParameterEntry>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public JDOQLParameterTable(Composite parent, int style) {
		super(parent, style);
	}
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText("Name");
		new TableColumn(table, SWT.LEFT).setText("JScript");
		TableLayout l = new TableLayout();
		l.addColumnData(new ColumnWeightData(1));
		l.addColumnData(new ColumnWeightData(1));
		table.setLayout(l);
		MenuManager mgr = new MenuManager();
		mgr.add(new Action("Add") {
			@Override
			public void run() {
				addJDOQLParameterEntry();
			}
		});
		mgr.add(new Action("Edit") {
			@Override
			public void run() {
				editCurrentJDOQLParameterEntry();
			}
		});
		mgr.add(new Action("Remove") {
			@Override
			public void run() {
				removeCurrentJDOQLParameterEntry();
			}
		});
		mgr.add(new Action("Clear") {
			@Override
			public void run() {
				removeAllJDOQLParameterEntries();
			}
		});
		Menu m = mgr.createContextMenu(table);
		table.setMenu(m);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setInput(entries);
	}

	public void addJDOQLParameterEntry() {
		EditEntryDialog dlg = new EditEntryDialog(RCPUtil.getActiveShell(), null);
		if (dlg.open() == Window.OK) {
			addJDOQLParameterEntry(dlg.getEntry());
		}
	}
	
	public void addJDOQLParameterEntry(JDOQLParameterEntry entry) {
		entries.add(entry);
		getTableViewer().setInput(entries);
	}
	
	public void removeCurrentJDOQLParameterEntry() {
		JDOQLParameterEntry entry = getFirstSelectedElement();
		if (entry == null)
			return;
		entries.remove(entry);
		getTableViewer().setInput(entries);
	}
	
	public void removeAllJDOQLParameterEntries() {
		entries.clear();
		getTableViewer().setInput(entries);
	}

	public void editCurrentJDOQLParameterEntry() {
		JDOQLParameterEntry entry = getFirstSelectedElement();
		if (entry == null)
			return;
//		entries.remove(entry);
		EditEntryDialog dlg = new EditEntryDialog(RCPUtil.getActiveShell(), entry);
		dlg.open();
		getTableViewer().setInput(entries);
	}
	
	public Map<String, Object> getParameterValues() {
		Map<String, Object> result = new HashMap<String, Object>();
		for (JDOQLParameterEntry entry : entries) {
			result.put(entry.getName(), entry.getValue());
		}
		return result;
	}
}
