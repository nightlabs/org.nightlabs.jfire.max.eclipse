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

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.QueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.search.AbstractProductTypeGroupQuery;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.store.search.VendorDependentQuery;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public abstract class AbstractProductTypeQuickListFilter
implements IProductTypeQuickListFilter
{
	private static Logger logger = Logger.getLogger(AbstractProductTypeQuickListFilter.class);

	public static String[] FETCH_GROUPS_QUERY_STORE = new String[] {
		FetchPlan.DEFAULT,
		BaseQueryStore.FETCH_GROUP_NAME,
		BaseQueryStore.FETCH_GROUP_DESCRIPTION,
		BaseQueryStore.FETCH_GROUP_SERIALISED_QUERIES
	};

	public static String[] FETCH_GROUPS_ARTICLE_CONTAINER_VENDOR = new String[] {
		FetchPlan.DEFAULT,
		ArticleContainer.FETCH_GROUP_VENDOR_ID,
	};

	private ListenerList selectionChangedListeners = new ListenerList();
	private IStructuredSelection selection = StructuredSelection.EMPTY;
	private QueryCollection<VendorDependentQuery> queryCollection;

	/**
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * Use this method to set the currently selected <tt>ProductTypeID</tt>. This
	 * method automatically notifies all interested listeners by firing a
	 * {@link SelectionChangedEvent}.
	 *
	 * @param productTypeID The new selected <tt>ProductTypeID</tt> or <tt>null</tt>.
	 */
	public void setSelectedProductTypeID(ProductTypeID productTypeID)
	{
		if (productTypeID == null)
			this.selection = StructuredSelection.EMPTY;
		else
			this.selection = new StructuredSelection(productTypeID);

		if (selectionChangedListeners.isEmpty())
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
		for (int i=0; i<selectionChangedListeners.size(); i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) selectionChangedListeners.getListeners()[i];
			listener.selectionChanged(event);
		}
	}

	/**
	 * Returns an instance of {@link IStructuredSelection} with exactly
	 * one instance of {@link ProductTypeID}.
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return selection;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(final ISelection selection)
	{
		if (getResultViewerControl() instanceof ISelectionProvider) {
			final ISelectionProvider selectionProvider = (ISelectionProvider) getResultViewerControl();
			if (selectionProvider instanceof ISelectionHandler) {
				final ISelectionHandler selectionHandler = (ISelectionHandler) selectionProvider;
				if (selectionHandler.canHandleSelection(selection)) {
					Display.getDefault().syncExec(new Runnable(){
						@Override
						public void run() {
							selectionHandler.setSelection(selection);
						}
					});
				}
			}
			else {
				Display.getDefault().syncExec(new Runnable(){
					@Override
					public void run() {
						selectionProvider.setSelection(selection);
					}
				});
			}
		}
	}

	/**
	 * Creates the Control which is then returned in {@link #createResultViewerControl(Composite)}
	 * @param parent the parent composite
	 * @return the Control which is then returned in {@link #createResultViewerControl(Composite)}
	 */
	protected abstract Control doCreateResultViewerControl(Composite parent);

	@Override
	public Control createResultViewerControl(Composite parent) {
		Control control = doCreateResultViewerControl(parent);
		if (control instanceof ISelectionProvider) {
			((ISelectionProvider)control).addSelectionChangedListener(
					getSelectionChangedListener());
		}
		return control;
	}

	/**
	 * Subclasses can override this method if they need a special ISelectionChangedListener
	 * for their implementation
	 * @return the SelectionListener for your implementation
	 */
	protected ISelectionChangedListener getSelectionChangedListener()
	{
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!(event.getSelection() instanceof IStructuredSelection))
					throw new ClassCastException("selection is an instance of "+(event.getSelection()==null?"null":event.getSelection().getClass().getName())+" instead of "+IStructuredSelection.class.getName()+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				Object elem = sel.getFirstElement();
				ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(elem);
				setSelectedProductTypeID(productTypeID);
			}
		};
	}

	@Override
	public boolean canHandleSelection(ISelection selection) {
		if (getResultViewerControl() instanceof ISelectionHandler) {
			ISelectionHandler selectionHandler = (ISelectionHandler) getResultViewerControl();
			return selectionHandler.canHandleSelection(selection);
		}
		else
			return false;
	}

	@Override
	public void search(ProgressMonitor monitor, boolean inJob) {
		if (! inJob) {
			new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter.job.search")) {  //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) {
					search(monitor);
					return Status.OK_STATUS;
				}
			}.schedule();
		} else {
			search(monitor);
		}
	}

//	public void setVendorID(AnchorID vendorID) {
//		getQuery().setVendorID(vendorID);
//	}

	/**
	 * performs the search with the Query returned by {@link #getQuery()}
	 * @param monitor the progressMonitor to show the progress
	 */
	protected abstract void search(ProgressMonitor monitor);

//	public abstract Class<?> getQueryResultClass();
	/**
	 * Returns the class of the resultType of the Query which is used for searching.
	 * @return the class of the resultType of the Query which is used for searching.
	 */
	public Class<?> getQueryResultClass() {
		return createQuery().getResultClass();
	}

	/**
	 * Returns the subclass of the {@link VendorDependentQuery} which is used
	 * for searching.
	 * @return the subclass of the {@link VendorDependentQuery} which is used
	 * for searching.
	 */
	protected abstract Class<? extends VendorDependentQuery> getQueryClass();

//	protected abstract VendorDependentQuery createQuery();

	/**
	 * Creates an instance of the subclass of VendorDependentQuery which is used for searching
	 * By default this is done by calling <code>getQueryClass().newInstance()</code>, if your implementation
	 * of VendorDependentQuery does not have an default constructor, override this method
	 * and return an instance of it.
	 * @return an instance of the VendorDependentQuery of the type return by {@link #getQueryClass()}
	 */
	protected VendorDependentQuery createQuery() {
		try {
			return getQueryClass().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Configures the Query return by {@link #createQuery()}.
	 * @param query the query to configure
	 */
	protected void configureQuery(VendorDependentQuery query) {
		if (query instanceof AbstractProductTypeQuery) {
			AbstractProductTypeQuery productTypeQuery = (AbstractProductTypeQuery) query;
			productTypeQuery.setSaleable(true);
		}
		if (query instanceof AbstractProductTypeGroupQuery) {
			AbstractProductTypeGroupQuery productTypeGroupQuery = (AbstractProductTypeGroupQuery) query;
			productTypeGroupQuery.setSaleable(true);
		}
	}

	@Override
	public QueryCollection<VendorDependentQuery> getQueryCollection(ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter.job.loadQuery"), 100); //$NON-NLS-1$
		if (queryCollection == null)
		{
			QueryStore defaultQueryStore;
			// TODO this should be done in the server in order to make it [nearly] impossible that 2 defaults are created!
			synchronized (AbstractProductTypeQuickListFilter.class) { // prevent having 2 defaultQueryStores due to multiple threads
				defaultQueryStore = QueryStoreDAO.sharedInstance().getDefaultQueryStore(
						getQueryResultClass(), Login.sharedInstance().getUserObjectID(),
						FETCH_GROUPS_QUERY_STORE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50));
				if (defaultQueryStore == null) {
					VendorDependentQuery query = createQuery();
					configureQuery(query);
					queryCollection = new QueryCollection<VendorDependentQuery>(getQueryResultClass());
					queryCollection.add(query);
					User owner = Login.sharedInstance().getUser(new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new org.eclipse.core.runtime.NullProgressMonitor());
					defaultQueryStore = new BaseQueryStore(owner, IDGenerator.nextID(BaseQueryStore.class), queryCollection);
					defaultQueryStore.setDefaultQuery(true);
					if (logger.isDebugEnabled()) {
						logger.debug("No default query store available, create one for resultClass = "+getQueryResultClass()+" and user "+Login.sharedInstance().getUserObjectID()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					defaultQueryStore = QueryStoreDAO.sharedInstance().storeQueryStore(defaultQueryStore,
							FETCH_GROUPS_QUERY_STORE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							true, new SubProgressMonitor(monitor, 50));
				}
			}
			if (queryCollection == null && defaultQueryStore != null) {
				QueryCollection qc = defaultQueryStore.getQueryCollection();
				if (qc != null) {
					queryCollection = qc;
					if (queryCollection.isEmpty()) {

						VendorDependentQuery query = createQuery();
						configureQuery(query);
						queryCollection.add(query);
					}
				}
			}
			if (queryCollection == null) {
				throw new IllegalStateException("QueryCollection is still null how can that happen!"); //$NON-NLS-1$
			}
		}
		monitor.done();
		return queryCollection;
	}

	@Override
	public void setQueryCollection(QueryCollection<VendorDependentQuery> queryCollection) {
		if (queryCollection == null)
			throw new IllegalArgumentException("QueryCollection must not be null!"); //$NON-NLS-1$

		if (!queryCollection.getResultClass().equals(getQueryResultClass())) {
			throw new IllegalArgumentException("The resultClass of the given queryCollection is "+queryCollection.getResultClass()+" but it should be "+getQueryResultClass()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		this.queryCollection = queryCollection;
	}

}
