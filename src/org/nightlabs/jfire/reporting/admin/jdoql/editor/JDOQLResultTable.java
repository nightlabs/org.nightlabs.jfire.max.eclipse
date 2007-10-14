/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.jdoql.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.admin.resource.Messages;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class JDOQLResultTable extends AbstractTableComposite {

	private static class ContentProvider extends TableContentProvider {

		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.table.TableContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection)
				return ((Collection)inputElement).toArray();
			return null;
		}		
	}
	
	private static class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			Object[] columns = null;
			if (element instanceof List)
				columns = ((List)element).toArray();
			else if (element instanceof Object[])
				columns = (Object[])element;
			else 
				return element != null ? element.toString() : "null"; //$NON-NLS-1$
			if (columns == null || columnIndex >= columns.length)
				return ""; //$NON-NLS-1$
			return String.valueOf(columns[columnIndex]);
		}
		
	}
	
	private List<TableColumn> tableColums = new ArrayList<TableColumn>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public JDOQLResultTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public JDOQLResultTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public JDOQLResultTable(Composite parent, int style, boolean initTable,
			int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
		// TODO Auto-generated constructor stub
	}

	private Collection currentResult;
	
	public void setInput(Collection result) {
		currentResult = result;
		createTableColumns(getTableViewer(), getTable());
		getTableViewer().setInput(result);
		layout(true, true);		
		if (getParent() != null)
			getParent().layout(true, true);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		if (currentResult == null)
			return;
		for (TableColumn column : tableColums) {
			column.dispose();
		}
		tableColums.clear();
		if (currentResult.size() <= 0)
			return;
		Object element = currentResult.iterator().next();
		if (element instanceof Object[]) {
			Object[] elements = (Object[])element;
			for (int i = 0; i < elements.length; i++) {
				TableColumn col = new TableColumn(table, SWT.LEFT); 
				col.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.jdoql.editor.JDOQLResultTable.columnPrefix")+i); //$NON-NLS-1$
				tableColums.add(col);
			}
		}
		else {
			TableColumn col = new TableColumn(table, SWT.LEFT); 
			col.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.jdoql.editor.JDOQLResultTable.columnOneText")); //$NON-NLS-1$
			tableColums.add(col);
		}
		int[] weights = new int[tableColums.size()];
		for (int i = 0; i < tableColums.size(); i++) {
			weights[i] = 1;
		}
		table.setLayout(new WeightedTableLayout(weights));
	}
	
	

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

}
