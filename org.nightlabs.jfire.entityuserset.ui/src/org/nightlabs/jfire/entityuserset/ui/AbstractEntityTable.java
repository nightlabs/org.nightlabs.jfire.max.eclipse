package org.nightlabs.jfire.entityuserset.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.entityuserset.ui.resource.Messages;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractEntityTable<Entity> 
extends AbstractTableComposite<Map.Entry<Entity, Boolean>> 
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
				return entities.entrySet().toArray();				
			}
			return super.getElements(inputElement);
		}
	}

	public abstract class AbstractEntityTableLabelProvider 
//	extends EmulatedNativeCheckBoxTableLabelProvider
	extends TableLabelProvider
	{
		/**
		 * @param viewer
		 */
		public AbstractEntityTableLabelProvider(TableViewer viewer) {
//			super(viewer);
			super();
		}

//		/* (non-Javadoc)
//		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getColumnImage(java.lang.Object, int)
//		 */
//		@Override
//		public Image getColumnImage(Object element, int columnIndex) 
//		{			
//			if (columnIndex == 0) {
//				Boolean b = ((Map.Entry<Entity, Boolean>) element).getValue();
//				if (b != null) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("getColumnImage() for element "+element+" and columnIndex "+columnIndex);
//					}			
//					return getCheckBoxImage(b);
//				}
//			}
//			return super.getColumnImage(element, columnIndex);
//		}
	}
	
	private static final Logger logger = Logger.getLogger(AbstractEntityTable.class);
	private IDirtyStateManager dirtyStateManager;
	
	/**
	 * @param parent
	 * @param style
	 * @param dirtyStateManager
	 */
	public AbstractEntityTable(Composite parent, int style, IDirtyStateManager dirtyStateManager) {
//		super(parent, style);
		super(parent, style, true, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER | SWT.CHECK);
		this.dirtyStateManager = dirtyStateManager;
	}

	/**
	 * The first checkbox column for selecting which entites should be included is already created by this class,
	 * implementations can add here additional columns for the table.
	 *  
	 * @param tableViewer the {@link TableViewer} which manages the given {@link Table}.
	 * @param table the {@link Table} which is managed by the {@link TableViewer}.
	 */
	protected abstract void createAdditionalTableColumns(TableViewer tableViewer, Table table);
	
	/**
	 * Creates a subclass of {@link AbstractEntityTableLabelProvider} for the tbaleViwer to show the labels 
	 * for the displayed entities.
	 * {@link AbstractEntityTableLabelProvider} already shows the check box image in the first column, subclasses
	 * must therefore only care about returning values for the additional created columns, 
	 * by the method {@link #createAdditionalTableColumns(TableViewer, Table)}. 
	 * 
	 * @param tableViewer the {@link TableViewer} which manages the {@link Table}.
	 * @return an implementation of {@link AbstractEntityTableLabelProvider} which will be used as labelProvider for
	 * the given {@link TableViewer}.
	 */
	protected abstract AbstractEntityTableLabelProvider createEntityTableLabelProvider(TableViewer tableViewer);
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) 
	{
		TableColumn checkBoxColumn = new TableColumn(table, SWT.NONE);
		checkBoxColumn.setText(""); //$NON-NLS-1$
		checkBoxColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.entityuserset.ui.AbstractEntityTable.checkboxColumn.tooltip")); //$NON-NLS-1$
		addCheckStateChangedListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableItem = ((TableItem) e.item);
				boolean checked = tableItem.getChecked();
				Map.Entry<Entity, Boolean> entry = (Map.Entry<Entity, Boolean>) tableItem.getData();
				entry.setValue(checked);
				dirtyStateManager.markDirty();
				if (logger.isDebugEnabled()) {
					logger.debug("setValue "+checked+" for entity "+entry.getKey()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});		
		
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
//				AbstractEntityTable.this.dirtyStateManager.markDirty();
//				getTableViewer().refresh(element, true);
//				if (logger.isDebugEnabled()) {
//					logger.debug("doSetValue() for element "+element+" and value "+value);
//				}
//			}
//		});
		
		createAdditionalTableColumns(tableViewer, table);
	}
	
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
		setInput(entities);
		if (entities != null) {
			Collection<Map.Entry<Entity, Boolean>> checkedElements = new ArrayList<Map.Entry<Entity, Boolean>>();
			for (Map.Entry<Entity, Boolean> entry : entities.entrySet()) {
				if (entry.getValue()) {
					checkedElements.add(entry);
				}
			}
			setCheckedElements(checkedElements);			
		}
	}
}
