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
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
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
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

/**
 * A View for a configurable quick-list of sellable products.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class ProductTypeQuickListView
extends LSDViewPart
implements ISelectionProvider
{
	public static final String ID_VIEW = ProductTypeQuickListView.class.getName();

	private XComposite wrapper;
	private List<Boolean> filterSearched = new ArrayList<Boolean>();
	private List<IProductTypeQuickListFilter> filters = new ArrayList<IProductTypeQuickListFilter>();
	private TabFolder tabFolder;
	private IStructuredSelection selection = StructuredSelection.EMPTY;
	private AnchorID vendorID = null;
	
	public ProductTypeQuickListView() {
		super();
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
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
			IProductTypeQuickListFilter filter = (filters.get(0));
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

		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				ArticleContainer.class, notificationListenerArticleContainerSelected);

		wrapper.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE,
						ProductType.class, selectionListener);

				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE,
						ProductType.class, notificationListenerArticleContainerSelected);
			}
		});
		refresh();
//		getSite().setSelectionProvider(this);
	}

	private NotificationListener notificationListenerArticleContainerSelected = new NotificationAdapterJob("Selecting vendor") {
		public void notify(NotificationEvent event) {
			getProgressMonitorWrapper().beginTask("Selecting vendor", 100);

			ArticleContainer articleContainer = null;

			if (event.getSubjects().isEmpty())
				getProgressMonitorWrapper().worked(100);
			else
				articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
						(ArticleContainerID)event.getFirstSubject(),
						AbstractProductTypeQuickListFilter.FETCH_GROUPS_ARTICLE_CONTAINER_VENDOR,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(getProgressMonitorWrapper(), 100));

			AnchorID newVendorID = articleContainer == null ? null : articleContainer.getVendorID();
			if (!Util.equals(vendorID, newVendorID)) {
				vendorID =newVendorID; 
				// vendor changed => all search results are outdated
				wrapper.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < filterSearched.size(); ++i)
							filterSearched.set(i, Boolean.FALSE);

						refresh();
					}
				});
			}

		}
	};

	private ISelectionChangedListener filterSelectionListener = new ISelectionChangedListener() {
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

			for (int i=0; i<selectionChangedListeners.size(); i++) {
				ISelectionChangedListener listener = (ISelectionChangedListener) selectionChangedListeners.getListeners()[i];
				listener.selectionChanged(selEvent);
			}

			NotificationEvent event = new NotificationEvent(
					ProductTypeQuickListView.this, TradePlugin.ZONE_SALE, selectedProductTypeID,
					selectedProductTypeID == null ? ProductTypeID.class : null);
			SelectionManager.sharedInstance().notify(event);

			if (selEvent.getSource() instanceof IProductTypeQuickListFilter) {
				IProductTypeQuickListFilter filter = (IProductTypeQuickListFilter) selEvent.getSource();
				// selected filter is not active filter
				if (!getSelectedFilter().equals(filter)) {
					for (int i=0; i<tabFolder.getItemCount(); i++) {
						TabItem tabItem = tabFolder.getItem(i);
						if (tabItem.getData() instanceof IProductTypeQuickListFilter) {
							IProductTypeQuickListFilter f = (IProductTypeQuickListFilter) tabItem.getData();
							if (f.equals(filter)) {
								tabSelectProgrammtically = true;
								tabFolder.setSelection(i);
								refresh(false);
								tabSelectProgrammtically = false;								
							}
						}
					}
				}				
			}
		}
	};

	private boolean tabSelectProgrammtically = false;

	// To listen for changes from outside
	private NotificationListener selectionListener = new NotificationAdapterCallerThread(){
		public void notify(NotificationEvent notificationEvent) {
			if (!notificationEvent.getSource().equals(ProductTypeQuickListView.this)) {
				Set<Object> subjects = notificationEvent.getSubjects();
				setSelection(new StructuredSelection(subjects));
			}
		}
	};

	private SelectionListener tabSelectionListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			if (!tabSelectProgrammtically) {
				TabItem selectedItem = tabFolder.getSelection()[0];
				IProductTypeQuickListFilter filter = (IProductTypeQuickListFilter) selectedItem.getData();
				ISelection selection = filter.getSelection();
				SelectionChangedEvent event = new SelectionChangedEvent(filter, selection);
				filterSelectionListener.selectionChanged(event);
			}
			refresh(false);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	public IProductTypeQuickListFilter getSelectedFilter() {
		int selection = tabFolder == null ? 0 : tabFolder.getSelectionIndex();
		if (selection >= 0 && selection < filters.size())
			return filters.get(selection);
		return null;
	}

	public boolean didSelectedFilterSearch() {
		int selection = tabFolder == null ? 0 : tabFolder.getSelectionIndex();
		if (selection >= 0 && selection < filters.size())
			return (filterSearched.get(selection)).booleanValue();
		return false;
	}

	public void setSelectedFilterSearched(boolean searched) {
		int selection = tabFolder == null ? 0 : tabFolder.getSelectionIndex();
		setSelectedFilterSearched(searched, selection);
	}

	private void setSelectedFilterSearched(boolean searched, int tabIndex) {
		if (tabIndex >= 0 && tabIndex < filters.size())
			filterSearched.set(tabIndex, new Boolean(searched));
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
						filter.getQuery(monitor).setVendorID(vendorID);
						filter.search(monitor, true);
						return Status.OK_STATUS;
					}
				}.schedule();

				setSelectedFilterSearched(true);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	private ListenerList selectionChangedListeners = new ListenerList();	

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
	public void setSelection(final ISelection selection)
	{
//		// Iterate over all filters and set the selection if they can handle it
//		for (IProductTypeQuickListFilter filter : filters) {
//		if (filter.canHandleSelection(selection)) {
//		filter.setSelection(selection);
//		}
//		}
		Set<ObjectID> objectIDs = SelectionUtil.getObjectIDs(selection);
		if (!objectIDs.isEmpty()) {
			for (ObjectID objectID : objectIDs) {
				Class<? extends Object> clazz = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(objectID);
				for (int i=0; i<filters.size(); i++) {
					final IProductTypeQuickListFilter filter = filters.get(i);
					Set<Class<? extends Object>> classes = filter.getClasses();
					if (classes.contains(clazz)) {
						// if not searched before, perform search first
						if (!filterSearched.get(i)) 
						{
							final int index = i;
							new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.ProductTypeQuickListView.refresh.job.name")) { //$NON-NLS-1$
								@Override
								protected IStatus run(ProgressMonitor monitor) {
									filter.search(monitor, false);
									setSelectedFilterSearched(true, index);
									if (filter.canHandleSelection(selection)) {
										filter.setSelection(selection);
									}
									return Status.OK_STATUS;
								}
							}.schedule();							
						}
						// already searched before, just check if can handle selection
						else {
							if (filter.canHandleSelection(selection)) {
								filter.setSelection(selection);
							}							
						}
					}
				}
			}
		}
	}

}
