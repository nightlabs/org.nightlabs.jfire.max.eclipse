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

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public abstract class AbstractProductTypeQuickListFilter
implements IProductTypeQuickListFilter
{
	private ListenerList selectionChangedListeners = new ListenerList();

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

	private IStructuredSelection selection = StructuredSelection.EMPTY;

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
	public void setSelection(ISelection selection)
	{
		if (getResultViewerControl() instanceof ISelectionProvider) {
			ISelectionProvider selectionProvider = (ISelectionProvider) getResultViewerControl();
			if (selectionProvider instanceof ISelectionHandler) {
				ISelectionHandler selectionHandler = (ISelectionHandler) selectionProvider;
				if (selectionHandler.canHandleSelection(selection)) {
					selectionHandler.setSelection(selection);
				}				
			} else {
				selectionProvider.setSelection(selection);
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
	
	public void search(ProgressMonitor monitor, boolean inJob) {
		if (inJob) {
			new Job(Messages.getString("org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter.job.search")) {  //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					search(new ProgressMonitorWrapper(monitor));
					return Status.OK_STATUS;
				}
			}.schedule();			
		} else {
			search(monitor);
		}
	}
	
	protected abstract void search(ProgressMonitor monitor);
}
