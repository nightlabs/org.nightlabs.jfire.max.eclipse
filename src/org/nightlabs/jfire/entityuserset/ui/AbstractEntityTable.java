package org.nightlabs.jfire.entityuserset.ui;

import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.EmulatedNativeCheckBoxTableLabelProvider;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractEntityTable<Entity> 
extends AbstractTableComposite<Entity> 
{
	class ContentProvider extends ArrayContentProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ArrayContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) 
		{
			if (inputElement instanceof Map) {
				Map<Entity, Boolean> entities = (Map<Entity, Boolean>) inputElement;
				return entities.keySet().toArray();
			}
			return super.getElements(inputElement);
		}
	}

	public abstract class AbstractEntityTableLabelProvider 
	extends EmulatedNativeCheckBoxTableLabelProvider
	{
		/**
		 * @param viewer
		 */
		public AbstractEntityTableLabelProvider(TableViewer viewer) {
			super(viewer);
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) 
		{
			if (columnIndex == 0) {
				Boolean b = entities.get(element);
				if (b != null) {
					return getCheckBoxImage(b);
				}
			}
			return super.getColumnImage(element, columnIndex);
		}
	}
	
	private Map<Entity, Boolean> entities;
	
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractEntityTable(Composite parent, int style) {
		super(parent, style, true, AbstractTableComposite.DEFAULT_STYLE_MULTI_BORDER | SWT.CHECK);
//		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) 
	{
		TableColumn checkBoxColumn = new TableColumn(table, SWT.NONE);
		checkBoxColumn.setText("Check");
		checkBoxColumn.setToolTipText("Check if the selected object should be included or not");
//		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.LEFT);
//		col1.getColumn().setResizable(false);
//		col1.getColumn().setText(""); //$NON-NLS-1$
//		col1.setEditingSupport(new CheckboxEditingSupport<Map.Entry<Entity, Boolean>>(tableViewer) {
//			@Override
//			protected boolean doGetValue(Map.Entry<Entity, Boolean> element) {
//				return element.getValue().booleanValue();
//			}
//
//			@Override
//			protected void doSetValue(Map.Entry<Entity, Boolean> element, boolean value) {
//				element.setValue(value);
////				AbstractEntityTable.this.dirtyStateManager.markDirty();
//			}
//		});
		
		createAdditionalTableColumns(tableViewer, table);
	}

	protected abstract void createAdditionalTableColumns(TableViewer tableViewer, Table table);
	
	protected abstract AbstractEntityTableLabelProvider createEntityTableLabelProvider(TableViewer tableViewer);
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) 
	{
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(createEntityTableLabelProvider(tableViewer));
	}

	public void setEntityInput(Map<Entity, Boolean> entities) {
		this.entities = entities;
		setInput(entities);
	}
}
