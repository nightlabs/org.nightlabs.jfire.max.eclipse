package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * Abstract implementation of an {@link AbstractTableComposite} which display {@link ProductType}s
 * and can handle {@link ISelection}s which contain {@link ProductTypeID}s.
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeTable<P extends ProductType>
extends AbstractTableComposite<P> 
implements ISelectionHandler
{
	public class ContentProvider 
	implements IStructuredContentProvider 
	{
		public Object[] getElements(Object inputElement) 
		{
			if (inputElement instanceof Collection) 
			{
				Collection<P> collection = (Collection) inputElement;
				return collection.toArray();
			}
			else
				throw new IllegalArgumentException("AbstractProductTypeTable.ContentProvider expects a Collection as inputElement. Recieved "+inputElement.getClass().getName()); //$NON-NLS-1$
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	public class LabelProvider 
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
	 */
	public AbstractProductTypeTable(Composite parent) {
//		this(parent, null, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
		super(parent, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public AbstractProductTypeTable(Composite parent, int style) {
//		this(parent, null, style);
		super(parent, style);
	}
	
//	/**
//	 * @param parent
//	 * @param filter
//	 */
//	public AbstractProductTypeTable(Composite parent, AbstractProductTypeQuickListFilter filter) {
//		this(parent, filter, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
//	}
//	
//	/**
//	 * @param parent
//	 * @param filter
//	 * @param style
//	 */
//	public AbstractProductTypeTable(Composite parent, AbstractProductTypeQuickListFilter filter, int style) {
//		super(parent, style);
//		this.filter = filter;
//		if (filter != null) {
//			getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
//				public void selectionChanged(SelectionChangedEvent event)
//				{
//					if (!(event.getSelection() instanceof IStructuredSelection))
//						throw new ClassCastException("selection is an instance of "+(event.getSelection()==null?"null":event.getSelection().getClass().getName())+" instead of "+IStructuredSelection.class.getName()+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//
//					Object elem = getFirstSelectedElement();
//					ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(elem);
//					AbstractProductTypeTable.this.filter.setSelectedProductTypeID(productTypeID);						
//				}
//			});			
//		}
//	}

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
		Set<ProductTypeID> typeIDs = getProductTypesIDs(selection);
		if (typeIDs.isEmpty()) {
			super.setSelection(selection);
		}
		else {
			final Set<ProductTypeID> productTypeIDs = typeIDs;		
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					List<P> productTypes = (List<P>) ProductTypeDAO.sharedInstance().
					getProductTypes(productTypeIDs, new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					superSetSelection(productTypes);
				}
			});
		}
	}

	protected void superSetSelection(List<P> elements) {
		super.setSelection(elements, true);
	}
	
	public boolean canHandleSelection(ISelection selection) 
	{
		Set<ProductTypeID> typeIDs = getProductTypesIDs(selection);
		if (!typeIDs.isEmpty()) {
			for (ProductTypeID productTypeID : typeIDs) {
				Class productTypeClass = JDOObjectID2PCClassMap.sharedInstance().
					getPersistenceCapableClass(productTypeID);
				if (getProductTypeClass().equals(productTypeClass)) {
					return true;
				}
			}
		}
		return false;
	}
		
	protected Set<ProductTypeID> getProductTypesIDs(ISelection selection) 
	{
		Set<ProductTypeID> typeIDs = new HashSet<ProductTypeID>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;			
			for (Object object : sel.toList()) {
				if (object instanceof Collection) {
					Collection set = (Collection) object;
					for (Object setEntry : set) {
						if (setEntry instanceof ProductTypeID) {
							typeIDs.add((ProductTypeID)setEntry);
						}
					}
				}
				else if (object instanceof ProductTypeID) {
					typeIDs.add((ProductTypeID)object);
				}
			}
		}
		return typeIDs;
	}
	
	/**
	 * Returns the Class of the {@link ProductType} this Table is displaying.
	 * @return the Class of the {@link ProductType} this Table is displaying
	 */
	public abstract Class<P> getProductTypeClass();
}
