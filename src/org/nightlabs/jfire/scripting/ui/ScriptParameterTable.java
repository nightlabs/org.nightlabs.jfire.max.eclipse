/**
 *
 */
package org.nightlabs.jfire.scripting.ui;

import java.util.Collection;
import java.util.EnumSet;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.scripting.IScriptParameter;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.ui.resource.Messages;
import org.nightlabs.util.CollectionUtil;



/**
 * A table displaying ScriptParameters.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptParameterTable
extends AbstractTableComposite<ScriptParameter>
{
	private static class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ScriptParameter) {
				ScriptParameter parameter = (ScriptParameter)element;
				switch (columnIndex) {
				case 0: return parameter.getScriptParameterID();
				case 1: return parameter.getScriptParameterClassName();
				}
			}
			return "";
		}

		@Override
		public String getText(Object element) {
			return getColumnText(element,0);
		}
	}

	private EnumSet<ScriptParameterTableOption> scriptParameterTableOptions;

	private static EnumSet<ScriptParameterTableOption> createScriptParameterTableOptions(ScriptParameterTableOption ... options)
	{
		if (options == null || options.length == 0)
			return EnumSet.noneOf(ScriptParameterTableOption.class);

		return EnumSet.of(options[0], options);
	}

	public ScriptParameterTable(Composite parent, int style, ScriptParameterTableOption ... options) {
		super(parent, style, false);
		this.scriptParameterTableOptions = createScriptParameterTableOptions(options);
		initTable();
	}

	protected ScriptParameterTable(Composite parent, int style, boolean initTable, ScriptParameterTableOption ... options) {
		super(parent, style, false);
		this.scriptParameterTableOptions = createScriptParameterTableOptions(options);

		if (initTable)
			initTable();
	}

	protected ScriptParameterTable(Composite parent, int style, boolean initTable, int viewerStyle, ScriptParameterTableOption ... options)
	{
		super(parent, style, false, viewerStyle);
		this.scriptParameterTableOptions = createScriptParameterTableOptions(options);

		if (initTable)
			initTable();
	}

	public static final String KEY_COLUMN_ID = "param_id";
	public static final String VALUE_COLUMN_ID = "param_name";

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(final TableViewer tableViewer, Table table) {
		final TableColumn paramIDColumn = new TableColumn(table, SWT.LEFT);
		paramIDColumn.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.ScriptParameterTable.columnParameterID.name")); //$NON-NLS-1$

		final TableColumn paramNameColumn = new TableColumn(table, SWT.LEFT);
		paramNameColumn.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.ScriptParameterTable.columnParameterType.name")); //$NON-NLS-1$

		table.setLayout(new WeightedTableLayout(new int[]{1,1}));

		tableViewer.setColumnProperties(new String[] {KEY_COLUMN_ID, VALUE_COLUMN_ID});

		if (scriptParameterTableOptions.contains(ScriptParameterTableOption.editable)) {
			tableViewer.setCellEditors(new CellEditor[] {new TextCellEditor(table), new TextCellEditor(table)});
			tableViewer.setCellModifier(new ScriptParameterCellModifier());
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ScriptParamentTableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}


	/**
	 * Sets the input to the parameters of the given ScriptParameterSet.
	 *
	 * @param scriptParameterSet
	 */
	@Override
	public void setInput(Object input) {
		if (!(input instanceof ScriptParameterSet))
			throw new IllegalArgumentException("input must be an instance of ScriptParameterSet, but is: " + input); //$NON-NLS-1$
		setScriptParameterSet((ScriptParameterSet)input);
	}

	private ScriptParameterSet scriptParameterSet;
	public void setScriptParameterSet(ScriptParameterSet scriptParameterSet)
	{
		this.scriptParameterSet = scriptParameterSet;
		super.setInput(scriptParameterSet);
	}

	private class ScriptParameterCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			if(property.equals(VALUE_COLUMN_ID)){
				return true;
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			if(property.equals(VALUE_COLUMN_ID)){
				return ((ScriptParameter)element).getScriptParameterClassName();
			}
			return null;
		}

		@Override
		public void modify(Object item, String property, Object value) {
			TableItem tableItem = (TableItem)item;
			ScriptParameter scriptParameter = (ScriptParameter)tableItem.getData();

			if (property.equals(VALUE_COLUMN_ID)){
				scriptParameter.setScriptParameterClassName((String)value);
			}

			getTableViewer().update(scriptParameter, new String[] { property });
			fireModification();

		}


	}

	protected static class ScriptParamentTableContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ScriptParameterSet) {
				ScriptParameterSet scriptParameterSet = (ScriptParameterSet) inputElement;
				Collection<IScriptParameter> parameters = scriptParameterSet.getSortedParameters();

				return CollectionUtil.collection2TypedArray(parameters, IScriptParameter.class, false);
			}
			return null;
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}



	}

	private ListenerList modificationListeners = new ListenerList();

	public void addModificationListener(ModificationListener listener) {
		modificationListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.II18nTextEditor#removeModificationFinishedListener(org.nightlabs.base.ui.language.ModificationFinishedListener)
	 */
	public void removeModificationListener(ModificationListener listener) {
		modificationListeners.remove(listener);
	}

	private void fireModification()
	{
	   ModifyListenerEvent event =  new ModifyListenerEvent(this);
	   for( int i=0; i< modificationListeners.size(); i++){
		   ModificationListener modifylistener = (ModificationListener)modificationListeners.getListeners()[i];
		   modifylistener.ModifyTextListener(event);
	   }


	}


}
