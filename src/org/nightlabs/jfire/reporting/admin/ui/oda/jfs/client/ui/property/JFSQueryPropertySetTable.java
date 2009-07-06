/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.oda.jfs.IJFSQueryPropertySetMetaData;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;
import org.nightlabs.jfire.reporting.oda.jfs.IJFSQueryPropertySetMetaData.IEntry;

/**
 * A table Composite that shows and is able to manipulate the properties of a {@link JFSQueryPropertySet}.
 * It manages the properties as elements of type {@link JFSQueryPropertySetTableEntry}.
 * The table allows the editing of all property values. The property names can be edited as well
 * given that the property is not references in the meta-data of the current {@link JFSQueryPropertySet}.
 * The table also provides methods to add and remove elements,
 * however it only removes properties that don't come from the meta-data.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class JFSQueryPropertySetTable extends AbstractTableComposite<JFSQueryPropertySetTableEntry> {

	private static final Logger logger = Logger.getLogger(JFSQueryPropertySetTable.class);
	/**
	 * Create a new {@link JFSQueryPropertySetTable}.
	 *
	 * @param parent The parent to use.
	 * @param style The style to apply.
	 */
	public JFSQueryPropertySetTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Create a new {@link JFSQueryPropertySetTable}.
	 *
	 * @param parent The parent to use.
	 * @param style The style to apply.
	 * @param initTable Whether to initialize the table (i.e. set label/content-provider etc.).
	 */
	public JFSQueryPropertySetTable(Composite parent, int style,
			boolean initTable) {
		super(parent, style, initTable);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		nameColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.column.name.text")); //$NON-NLS-1$
		nameColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				JFSQueryPropertySetTableEntry entry = (JFSQueryPropertySetTableEntry) cell.getElement();
				cell.setText(entry.getName());
			}
		});

		TableViewerColumn valueColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		valueColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.column.value.text")); //$NON-NLS-1$
		valueColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				JFSQueryPropertySetTableEntry entry = (JFSQueryPropertySetTableEntry) cell.getElement();
				if (entry.getValue() != null && !"".equals(entry.getValue())) //$NON-NLS-1$
					cell.setText(entry.getValue());
				else
					cell.setText(""); //$NON-NLS-1$
			}
		});
		valueColumn.setEditingSupport(createValueEditingSupport());
		nameColumn.setEditingSupport(createNameEditingSupport());
		table.setLayout(new WeightedTableLayout(new int[] {1, 1}));
	}

	/**
	 * Creates the {@link EditingSupport} for the value column.
	 * It manages the value property within the {@link JFSQueryPropertySetTableEntry}
	 * elements of this table.
	 *
	 * @return The {@link EditingSupport} for the value column.
	 */
	protected EditingSupport createValueEditingSupport() {
		return new EditingSupport(getTableViewer()) {
			private TextCellEditor cellEditor;

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof JFSQueryPropertySetTableEntry;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (cellEditor == null) {
					cellEditor = new TextCellEditor(getTable());
				}
				return cellEditor;
			}

			@Override
			protected Object getValue(Object element) {
				String value = ((JFSQueryPropertySetTableEntry) element).getValue();
				if (value == null)
					return ""; //$NON-NLS-1$
				return value;
			}

			@Override
			protected void setValue(Object element, Object value) {
				((JFSQueryPropertySetTableEntry) element).setValue((String) value);
				getTableViewer().refresh(element, true);
			}
		};
	}

	/**
	 * Creates the {@link EditingSupport} for the name column.
	 * It manages the name property within the {@link JFSQueryPropertySetTableEntry}
	 * elements of this table. Only the names of properties that
	 * don't come from the meta-data of the Script referecned by the
	 * {@link JFSQueryPropertySet}.
	 *
	 * @return The {@link EditingSupport} for the value column.
	 */
	protected EditingSupport createNameEditingSupport() {
		return new EditingSupport(getTableViewer()) {
			private TextCellEditor cellEditor;

			@Override
			protected boolean canEdit(Object element) {
				return
					(element instanceof JFSQueryPropertySetTableEntry) &&
					!((JFSQueryPropertySetTableEntry) element).isFromMetaData();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (cellEditor == null) {
					cellEditor = new TextCellEditor(getTable());
				}
				return cellEditor;
			}

			@Override
			protected Object getValue(Object element) {
				String name = ((JFSQueryPropertySetTableEntry) element).getName();
				if (name == null)
					return ""; //$NON-NLS-1$
				return name;
			}

			@Override
			protected void setValue(Object element, Object value) {
				String name = (String) value;
				if (name == null || "".equals(name)) { //$NON-NLS-1$
					MessageDialog.openInformation(
							getShell(),
							Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.dialog.emptyProperty.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.dialog.emptyProperty.message1") + //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.dialog.emptyProperty.message2")); //$NON-NLS-1$
					return;
				}
				JFSQueryPropertySetTableEntry entry = (JFSQueryPropertySetTableEntry) element;
				for (JFSQueryPropertySetTableEntry checkEntry : entries) {
					if (entry != checkEntry && name.equals(checkEntry.getName())) {
						MessageDialog.openInformation(
								getShell(),
								Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.dialog.duplicateProperty.title"), //$NON-NLS-1$
								Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.dialog.duplicateProperty.message1") + //$NON-NLS-1$
								Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.JFSQueryPropertySetTable.dialog.duplicateProperty.message2")); //$NON-NLS-1$
						return;
					}
				}
				entry.setName(name);
				getTableViewer().refresh(element, true);
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
//		tableViewer.setSorter(new ViewerSorter() {
//			@Override
//			public int category(Object element) {
//				return ((JFSQueryPropertySetTableEntry) element).isFromMetaData() ? 0 : 1;
//			}
//		});
	}

	/**
	 * Query the {@link IJFSQueryPropertySetMetaData} of the Script referenced by the given
	 * {@link JFSQueryPropertySet}. This currently accesses the {@link ReportManager} bean
	 * directly.
	 *
	 * @param queryPropertySet The {@link JFSQueryPropertySet} to get the meta-data for.
	 * @return The {@link IJFSQueryPropertySetMetaData} for the Script referenced by the given {@link JFSQueryPropertySet}.
	 */
	protected IJFSQueryPropertySetMetaData getJFSQueryPropertySetMetaData(JFSQueryPropertySet queryPropertySet) {
		try {
			ReportManagerRemote rm = JFireEjb3Factory.getRemoteBean(ReportManagerRemote.class, Login.getLogin().getInitialContextProperties());
			return rm.getJFSQueryPropertySetMetaData(queryPropertySet.getScriptRegistryItemID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initialize this table with the properties of the given queryPropertySet.
	 *
	 * @param queryPropertySet The {@link JFSQueryPropertySet} to initialize the table with.
	 */
	public void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet) {
		Set<String> definedProps = new HashSet<String>(queryPropertySet.getProperties().keySet());
		IJFSQueryPropertySetMetaData metaData = getJFSQueryPropertySetMetaData(queryPropertySet);
		List<JFSQueryPropertySetTableEntry> entries = new LinkedList<JFSQueryPropertySetTableEntry>();

		// All entries from the meta-data first
		for (IEntry metaDataEntry : metaData.getEntries()) {
			if (definedProps.contains(metaDataEntry.getName())) {
				// value set for a property from the meta-data
				String value = queryPropertySet.getProperties().get(metaDataEntry.getName());
				JFSQueryPropertySetTableEntry entry = new JFSQueryPropertySetTableEntry(
						true,
						metaDataEntry.isRequired(),
						metaDataEntry.getName());
				entry.setValue(value);
				entries.add(entry);
				definedProps.remove(metaDataEntry.getName());
			} else {
				// value NOT set for a property from the meta-data
				JFSQueryPropertySetTableEntry entry = new JFSQueryPropertySetTableEntry(
						true,
						metaDataEntry.isRequired(),
						metaDataEntry.getName());
				entries.add(entry);
			}
		}
		// then all other defined properties
		for (String definedPropName : definedProps) {
			String value = queryPropertySet.getProperties().get(definedPropName);
			JFSQueryPropertySetTableEntry entry = new JFSQueryPropertySetTableEntry(
					false,
					false,
					definedPropName);
			entry.setValue(value);
			entries.add(entry);
		}

		setInput(entries);
	}

	private List<JFSQueryPropertySetTableEntry> entries = null;

	@SuppressWarnings("unchecked")
	@Override
	public void setInput(Object input) {
		if (input instanceof List) {
			entries = (List<JFSQueryPropertySetTableEntry>) input;
			super.setInput(input);
		} else {
			logger.error(this.getClass().getName() + " received an unexpected input type " + input.getClass().getName()); //$NON-NLS-1$
		}
	}

	/**
	 * Removes the first selected entry from this table.
	 * Note, that this will have no effect if the first
	 * selected element is a property referenced in
	 * the meta-data of the curren {@link JFSQueryPropertySet}.
	 */
	public void removeFirstSelectedEntry() {
		JFSQueryPropertySetTableEntry entry = getFirstSelectedElement();
		if (entry == null)
			return;
		entries.remove(entry);
		refresh(true);
	}

	/**
	 * Checks if the given name can be used as property name
	 * in the given set of properties.
	 *
	 * @param name The name to check.
	 * @return Whether the given name can be used as property name in the current set.
	 */
	private boolean checkNewPropertyName(String name) {
		for (JFSQueryPropertySetTableEntry entry : entries) {
			if (name.equals(entry.getName()))
				return false;
		}
		return true;
	}

	/**
	 * Adds a new entry to the list of properties of this table
	 * and will select its name column for editing.
	 * @return The newly created {@link JFSQueryPropertySetTableEntry}.
	 */
	public JFSQueryPropertySetTableEntry createNewEntry() {
		String namePrefix = "NewProperty"; //$NON-NLS-1$
		int i = 1;
		while (!checkNewPropertyName(namePrefix + i)) i++;
		JFSQueryPropertySetTableEntry entry = new JFSQueryPropertySetTableEntry(
				false, false,
				namePrefix + i
			);
		entries.add(entry);
		refresh(true);
		getTableViewer().editElement(entry, 0);
		return entry;
	}

	/**
	 * Collect the properties from the table as they should be set
	 * to a {@link JFSQueryPropertySet}.
	 *
	 * @return The properties collected from the table.
	 */
	public Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<String, String>();
		if (entries != null) {
			for (JFSQueryPropertySetTableEntry entry : entries) {
				result.put(entry.getName(), entry.getValue());
			}
		}
		return result;
	}
}