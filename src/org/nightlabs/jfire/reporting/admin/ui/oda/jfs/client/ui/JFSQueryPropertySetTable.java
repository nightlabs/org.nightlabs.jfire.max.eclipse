package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;

/**
 * Generic Composite to edit properties in a table.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class JFSQueryPropertySetTable extends AbstractTableComposite {

	private static final String COL_KEY = "Col-Key"; //$NON-NLS-1$
	private static final String COL_VAL = "Col-Val"; //$NON-NLS-1$
	
	public static class LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int colIdx) {
			return null;
		}

		public String getColumnText(Object element, int colIdx) {
			if (element instanceof Map.Entry) {
				switch (colIdx) {
					case 0: return ((Map.Entry)element).getKey().toString();
					case 1: return ((Map.Entry)element).getValue().toString();
				}
			} else {
				if (colIdx == 0)
					return Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetTable.addKeyColumnText"); //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}

		public void addListener(ILabelProviderListener arg0) {
		}
		public void dispose() {
		}
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}
		public void removeListener(ILabelProviderListener arg0) {
		}
	}
	
	public static class ContentProvider implements IStructuredContentProvider {

		private TreeMap<String, String> properties;
		
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Map) {
				this.properties = new TreeMap<String, String>((Map<String, String>)inputElement);
				Object[] result = new Object[((Map)inputElement).entrySet().size() + 1];
				int i = 0;
				for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
					result[i++] = iter.next();
				}
				// we put a dummy object as last element for the add new key cell editor
				result[result.length-1] = new Object();
				return result;
			}
			return Collections.EMPTY_MAP.entrySet().toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
		
		public Map<String, String> getProperties() {
			return properties;
		}
		
		@SuppressWarnings("unchecked")
		public void setProperty(String name, String value) {
			properties.put(name, value);
		}
		
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public JFSQueryPropertySetTable(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Returns the Properties from the ContentProvider
	 */
	public Map<String, String> getProperties() {
		return getContentProvider().getProperties();
	}
	
	public ContentProvider getContentProvider() {
		return (ContentProvider)tableViewer.getContentProvider();
	}


	private class CellModifier implements ICellModifier {
		public boolean canModify(Object element, String property) {
			if (element instanceof Map.Entry) {
				return COL_VAL.equals(property);
			}
			return COL_KEY.equals(property);
		}



		public Object getValue(Object element, String property) {
			if (element instanceof Map.Entry) {
				if (COL_VAL.equals(property))
					return ((Map.Entry)element).getValue();
			}
			return Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetTable.newPropertyNameLabel"); //$NON-NLS-1$
		}



		public void modify(Object tableElement, String property, Object value) {
			Object element = ((TableItem)tableElement).getData();
			if (element instanceof Map.Entry) {
				String newVal = (String)value;
				String name = (String)((Map.Entry)element).getKey();
				getContentProvider().setProperty(name, newVal);
			}
			else {
				getContentProvider().setProperty((String)value, ""); //$NON-NLS-1$
			}
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					setInput(getContentProvider().getProperties());
				}
			});
		}
	}



	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetTable.keyColumn.text")); //$NON-NLS-1$
		
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFSQueryPropertySetTable.valueColumn.text")); //$NON-NLS-1$
		
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1));
		layout.addColumnData(new ColumnWeightData(1));
		table.setLayout(layout);
	}



	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		
		tableViewer.setColumnProperties(new String[] {COL_KEY, COL_VAL});
		tableViewer.setCellEditors(new CellEditor[] {new TextCellEditor(table), new TextCellEditor(table)});
		tableViewer.setCellModifier(new CellModifier());
	}

}

