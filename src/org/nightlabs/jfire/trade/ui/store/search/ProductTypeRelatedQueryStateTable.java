package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.celleditor.TristateCheckboxCellEditor;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.JFaceUtil;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.store.search.ISaleAccessQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * Simple Table displaying a Collection of ProductTypeStateDescriptions of an
 * AbstractProductTypeQuery.
 * <p><b>Note:</b> This table only writes through to the query. The other way is not implemented,
 * since it is not necessary as of now.
 * </p>
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ProductTypeRelatedQueryStateTable
	extends AbstractTableComposite<ISaleAccessQuery>
{
	public ProductTypeRelatedQueryStateTable(Composite parent, int style)
	{
		super(parent, style);
	}

	public ProductTypeRelatedQueryStateTable(Composite parent, int style, boolean initTable)
	{
		super(parent, style, initTable);
	}

	public ProductTypeRelatedQueryStateTable(Composite parent, int style, boolean initTable, int viewerStyle)
	{
		super(parent, style, initTable, viewerStyle);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(final TableViewer tableViewer, Table table)
	{
		table.setLayout(new WeightedTableLayout(new int[] { 1, 20 }));
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeRelatedQueryStateTable.column.filtered.text")); //$NON-NLS-1$
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return ""; //$NON-NLS-1$
			}

			@Override
			public Image getImage(Object element)
			{
				// display the image of the checkbox corresponding to the value of the corresponding state
				// if the state field is enabled in the query.
				// Otherwise display an unchecked disabled checkbox.
				final ProductTypeStateDescription desc = (ProductTypeStateDescription) element;
				if (desc.isEnabled())
				{
					Boolean value = desc.getValue();
					if (value == null)
						value = Boolean.FALSE;

					return JFaceUtil.getCheckBoxImage(tableViewer, value, true);
				}
				else
					return JFaceUtil.getCheckBoxImage(tableViewer, true, false);
			}
		});
		column.setEditingSupport(new EditingSupport(tableViewer)
		{
			private TristateCheckboxCellEditor cellEditor = new TristateCheckboxCellEditor();

			@Override
			protected boolean canEdit(Object element)
			{
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element)
			{
				return cellEditor;
			}

			@Override
			protected Object getValue(Object element)
			{
				final ProductTypeStateDescription stateDescription = (ProductTypeStateDescription) element;
				if (! stateDescription.isEnabled())
					return null;

				return stateDescription.getValue();
			}

			@Override
			protected void setValue(Object element, Object value)
			{
				final ProductTypeStateDescription stateDescription = (ProductTypeStateDescription) element;
				if (value == null)
				{
					stateDescription.setEnabled(false);
					tableViewer.update(stateDescription, null);
					return;
				}

				if (!stateDescription.isEnabled())
					stateDescription.setEnabled(true);

				final boolean enabled = ((Boolean) value).booleanValue();
				stateDescription.setValue(enabled);
				tableViewer.update(stateDescription, null);
			}
		});

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeRelatedQueryStateTable.column.state.text")); //$NON-NLS-1$
		column.setLabelProvider(new ColumnLabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				final ProductTypeStateDescription stateDescription = (ProductTypeStateDescription) element;
				return stateDescription.getName();
			}
		});

	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new IStructuredContentProvider()
		{
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
//				if (oldInput != null && oldInput instanceof AbstractProductTypeQuery)
//				{
//					final AbstractProductTypeQuery query = (AbstractProductTypeQuery) oldInput;
//					query.removeQueryChangeListener(changeListener);
//				}
//				if (newInput != null && newInput instanceof AbstractProductTypeQuery)
//				{
//					final AbstractProductTypeQuery query = (AbstractProductTypeQuery) newInput;
//					query.addQueryChangeListener(changeListener);
//				}
			}

			@Override
			public Object[] getElements(Object inputElement)
			{
				if (inputElement == null || !(inputElement instanceof ISaleAccessQuery))
					return new Object[0];

				final ISaleAccessQuery query = (ISaleAccessQuery) inputElement;
				ProductTypeStateDescription[] stateDescriptions = new ProductTypeStateDescription[5];
				stateDescriptions[0] = new ProductTypeStateDescription(
						query, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeRelatedQueryStateTable.column.confirmed.text"), AbstractProductTypeQuery.FieldName.confirmed); //$NON-NLS-1$
				stateDescriptions[1] = new ProductTypeStateDescription(
						query, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeRelatedQueryStateTable.column.published.text"), AbstractProductTypeQuery.FieldName.published); //$NON-NLS-1$
				stateDescriptions[2] = new ProductTypeStateDescription(
						query, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeRelatedQueryStateTable.column.saleable.text"), AbstractProductTypeQuery.FieldName.saleable); //$NON-NLS-1$
				stateDescriptions[3] = new ProductTypeStateDescription(
						query, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeRelatedQueryStateTable.column.closed.text"), AbstractProductTypeQuery.FieldName.closed); //$NON-NLS-1$

				stateDescriptions[4] = new ProductTypeStateDescription(
						query,
						"User is allowed to sell",
						AbstractProductTypeQuery.FieldName.permissionGrantedToSell
				);
				return stateDescriptions;
			}

			@Override
			public void dispose()
			{
			}
		});
	}

//	This seems to be unnecessary!
//	private PropertyChangeListener changeListener = new PropertyChangeListener()
//	{
//		@Override
//		public void propertyChange(PropertyChangeEvent evt)
//		{
//			if (evt instanceof QueryEvent)
//			{
//				final QueryEvent event = (QueryEvent) evt;
//				for (FieldChangeCarrier changedProperty : event.getChangedFields())
//				{
//					final String propName = changedProperty.getPropertyName();
//					if (AbstractProductTypeQuery.FieldName.confirmed.equals(propName))
//					{
//
//					}
//				}
//			}
//		}
//	};

}
