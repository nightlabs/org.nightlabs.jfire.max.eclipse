package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.Collection;
import java.util.Locale;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * Abstract Base class for {@link Table}s which display {@link ProductType}s.
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeTable<P extends ProductType>
extends AbstractTableComposite<P> 
{
	public static class ContentProvider 
	implements IStructuredContentProvider 
	{
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection) {
				return ((Collection)inputElement).toArray();
			}
			else
				throw new IllegalArgumentException("AbstractProductTypeTable.ContentProvider expects a Collection as inputElement. Recieved "+inputElement.getClass().getName()); //$NON-NLS-1$
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	public static class LabelProvider 
	extends org.eclipse.jface.viewers.LabelProvider 
	implements ITableLabelProvider 
	{
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			return ((ProductType)element).getName().getText(Locale.getDefault().getLanguage());
		}
	}	

	private AbstractProductTypeQuickListFilter filter;
	
	/**
	 * @param parent
	 * @param filter
	 */
	public AbstractProductTypeTable(Composite parent, AbstractProductTypeQuickListFilter filter) {
		this(parent, filter, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
	}
	
	/**
	 * @param parent
	 * @param filter
	 * @param style
	 */
	public AbstractProductTypeTable(Composite parent, AbstractProductTypeQuickListFilter filter, int style) {
		super(parent, style);
		this.filter = filter;
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (!(event.getSelection() instanceof IStructuredSelection))
					throw new ClassCastException("selection is an instance of "+(event.getSelection()==null?"null":event.getSelection().getClass().getName())+" instead of "+IStructuredSelection.class.getName()+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				Object elem = getFirstSelectedElement();
				ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(elem);
				AbstractProductTypeTable.this.filter.setSelectedProductTypeID(productTypeID);
			}
		});		
	}

	/**
	 * By default creates only one {@link TableColumn} which displays the productType name
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText("Name"); 
		TableLayout l = new TableLayout();
		l.addColumnData(new ColumnWeightData(1));
		table.setLayout(l);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	@Override
	public void setSelection(ISelection selection) {
		// TODO: check here if selection contains ProductTypeIDs and then 
		// obtain them with ProductTypeDAO.getProductTypes() and set the returned
		// collection as selection, which the viewer can then handle 
		super.setSelection(selection);
	}

}
