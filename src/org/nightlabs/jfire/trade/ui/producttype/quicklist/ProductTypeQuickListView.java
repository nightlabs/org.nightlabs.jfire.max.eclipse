/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A View for a configurable quick-list of sellable products.
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ProductTypeQuickListView
extends LSDViewPart
implements ISelectionProvider
{
	public static final String ID_VIEW = ProductTypeQuickListView.class.getName();

	private XComposite wrapper;	 
	private List<Boolean> filterSearched = new LinkedList<Boolean>();
	private List<IProductTypeQuickListFilter> filters = new ArrayList<IProductTypeQuickListFilter>();

	private TabFolder tabFolder;

	private IStructuredSelection selection = StructuredSelection.EMPTY;

	public ProductTypeQuickListView() {
		super();
//		try {
//		Login.getLogin();
//		} catch (LoginException e) {
//		throw new RuntimeException(e);
//		}
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		wrapper = new XComposite(parent, SWT.NONE);
		Collection<IProductTypeQuickListFilterFactory> factories = null;
		try {
			factories = ProductTypeQuickListFilterFactoryRegistry.sharedInstance().getProductQuickListFilterFactories();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		filters.clear();
		filterSearched.clear();
		for (IProductTypeQuickListFilterFactory factory : factories) {
			filterSearched.add(new Boolean(false));
			filters.add(factory.createProductTypeQuickListFilter());
		}
		if ( filters.size() == 1 ) {
			IProductTypeQuickListFilter filter = ((IProductTypeQuickListFilter)filters.get(0));
			filter.addSelectionChangedListener(filterSelectionListener);
			Label label = new Label(wrapper, SWT.NONE);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.setText(filter.getDisplayName());
			filter.createResultViewerControl(wrapper);
		} else {
			tabFolder = new TabFolder(wrapper, SWT.NONE);
			tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
			for (IProductTypeQuickListFilter filter : filters) {
				filter.addSelectionChangedListener(filterSelectionListener);
				TabItem filterTabItem = new TabItem(tabFolder, SWT.BORDER);				
				filterTabItem.setText(filter.getDisplayName());
				if (filter.createResultViewerControl(tabFolder) != null) {
					filterTabItem.setControl(filter.createResultViewerControl(tabFolder));
					filterTabItem.setData(filter);
				} else
					throw new IllegalStateException("filter.createResultViewerControl(tabFolder) == null"); //$NON-NLS-1$
			}
			tabFolder.addSelectionListener(tabSelectionListener);
		}
		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE, 
				ProductType.class, selectionListener);
		wrapper.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE, 
						ProductType.class, selectionListener);
			}
		});
		refresh();
//		getSite().setSelectionProvider(this);
	}

	ISelectionChangedListener filterSelectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent selEvent)
		{
			if (!(selEvent.getSelection() instanceof IStructuredSelection))
				throw new ClassCastException("Selection is an instance of "+(selEvent.getSelection()==null?"null":selEvent.getSelection().getClass().getName())+", but must be "+IStructuredSelection.class.getName()+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			ProductTypeID selectedProductTypeID = null;

			IStructuredSelection sel = (IStructuredSelection)selEvent.getSelection();
			for (Iterator<Object> it = sel.iterator(); it.hasNext(); ) {
				Object o = it.next();
				if (!(o instanceof ProductTypeID))
					throw new ClassCastException("At least one entry in the selection is not an instance of "+ProductTypeID.class.getName()+"! It is a "+(o == null?"null":o.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				if (selectedProductTypeID == null) // we select only the first one
					selectedProductTypeID = (ProductTypeID)o;
			}
			ProductTypeQuickListView.this.selection = sel;

			for (ISelectionChangedListener listener : selectionChangedListeners) {
				listener.selectionChanged(selEvent);
			}

			NotificationEvent event = new NotificationEvent(
					ProductTypeQuickListView.this, TradePlugin.ZONE_SALE, selectedProductTypeID,
					selectedProductTypeID == null ? ProductTypeID.class : null);
			SelectionManager.sharedInstance().notify(event);
		}
	};

	// To listen for changes from outside
	private NotificationListener selectionListener = new NotificationAdapterCallerThread(){
		public void notify(NotificationEvent notificationEvent) {
			if (!notificationEvent.getSource().equals(ProductTypeQuickListView.this)) { 
				Set subjects = notificationEvent.getSubjects();
				setSelection(new StructuredSelection(subjects));
			}
		}	
	};

	private SelectionListener tabSelectionListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			TabItem selectedItem = tabFolder.getSelection()[0];
			IProductTypeQuickListFilter filter = (IProductTypeQuickListFilter) selectedItem.getData();
			ISelection selection = filter.getSelection();
			SelectionChangedEvent event = new SelectionChangedEvent(filter, selection);
			filterSelectionListener.selectionChanged(event);
			refresh(false);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	public IProductTypeQuickListFilter getSelectedFilter() {
		int selection = tabFolder == null ? 0 : tabFolder.getSelectionIndex();
		if (selection >= 0 && selection < filters.size())
			return (IProductTypeQuickListFilter)filters.get(selection);
		return null;
	}

	public boolean didSelectedFilterSearch() {
		int selection = tabFolder == null ? 0 : tabFolder.getSelectionIndex();
		if (selection >= 0 && selection < filters.size())
			return ((Boolean)filterSearched.get(selection)).booleanValue();
		return false;
	}

	public void setSelectedFilterSearched(boolean searched) {
		int selection = tabFolder == null ? 0 : tabFolder.getSelectionIndex();
		if (selection >= 0 && selection < filters.size())
			filterSearched.set(selection, new Boolean(searched));
	}

	public void refresh() {
		refresh(true);
	}

	public void refresh(boolean force) {
		final IProductTypeQuickListFilter filter = getSelectedFilter();
		if (filter != null) {
			if ((!didSelectedFilterSearch()) || force) {	
				new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.ProductTypeQuickListView.refresh.job.name")) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) {
						filter.search(monitor);
						return Status.OK_STATUS;
					}
				}.schedule();

				setSelectedFilterSearched(true);
			}
		}
	}

//int selection = tabFolder.getSelectionIndex();
//if (selection >= 0 && selection < filters.size()) {
//IProductTypeQuickListFilter filter = (IProductTypeQuickListFilter)filters.get(selection);
//filter.search();
//}
//for (Iterator iter = filters.iterator(); iter.hasNext();) {
//IProductTypeQuickListFilter filter = (IProductTypeQuickListFilter) iter.next();
//filter.search();
//}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	private LinkedList<ISelectionChangedListener> selectionChangedListeners = new LinkedList<ISelectionChangedListener>();

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return selection;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
//		throw new UnsupportedOperationException("NYI");
		if (getSelectedFilter() != null) {
			// TODO: not only set selection for selected filter but all, and display this one 
			getSelectedFilter().setSelection(selection);
		}
	}

}
