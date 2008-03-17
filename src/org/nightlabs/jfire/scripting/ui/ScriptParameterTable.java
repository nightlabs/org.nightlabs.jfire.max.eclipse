/**
 *
 */
package org.nightlabs.jfire.scripting.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.ui.resource.Messages;

/**
 * A table displaying ScriptParameters.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptParameterTable extends AbstractTableComposite<ScriptParameter> {

//	private static class ContentProvider extends TableContentProvider {
//	}

	private static class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ScriptParameter) {
				ScriptParameter parameter = (ScriptParameter)element;
				switch (columnIndex) {
					case 0: return parameter.getScriptParameterID();
					case 1: return parameter.getScriptParameterClassName();
				}
			}
			return ""; //$NON-NLS-1$
		}

		@Override
		public String getText(Object element) {
			return getColumnText(element,0);
		}
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ScriptParameterTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public ScriptParameterTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public ScriptParameterTable(Composite parent, int style, boolean initTable,
			int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		(new TableColumn(table, SWT.LEFT)).setText(Messages.getString("org.nightlabs.jfire.scripting.ui.ScriptParameterTable.columnParameterID.name")); //$NON-NLS-1$
		(new TableColumn(table, SWT.LEFT)).setText(Messages.getString("org.nightlabs.jfire.scripting.ui.ScriptParameterTable.columnParameterType.name")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[]{1,1}));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	/**
	 * Sets the input to the parameters of the given ScriptParameterSet.
	 *
	 * @param scriptParameterSet
	 */
	public void setInput(ScriptParameterSet scriptParameterSet) {
		setInput(scriptParameterSet.getParameters());
	}

}
