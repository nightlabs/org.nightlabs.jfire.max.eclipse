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

import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Interface for filtering products within the ProductTypeQuickListView.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public interface IProductTypeQuickListFilter
//extends ISelectionProvider
extends ISelectionHandler
{
	/**
	 * Creates the Control that displays the filtered results
	 * to the user.
	 * 
	 * @param parent The parent composite of the ResultViewer
	 * @return The created Control
	 */
	Control createResultViewerControl(Composite parent);

	/**
	 * Returns the Control that displays the filtered results
	 * to the user.
	 * 
	 * @param parent The parent composite of the ResultViewer
	 * @return The created Control
	 */	
	Control getResultViewerControl();
	
	/**
	 * Returns the display name of this filter
	 * @return The display name of this filter
	 */
	String getDisplayName();

	// TODO should return the result instead of void, otherwise check for already
	// performed searches in setSelection() of ProductTypeQuickListView does not work reliably
	/** 
	 * Searches for <tt>ProductType</tt>s and refreshes the resultViewer.
	 */
	void search(ProgressMonitor monitor, boolean inJob);

	/**
	 * In your implementation of <tt>IProductTypeQuickListFilter</tt>, you must
	 * make sure to select {@link org.nightlabs.jfire.store.id.ProductTypeID} instances
	 * and fire events with {@link org.eclipse.jface.viewers.IStructuredSelection} instances
	 * which contain these <tt>ProductTypeID</tt>s.
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	void addSelectionChangedListener(ISelectionChangedListener listener);

	/**
	 * This method should be implemented. Please check in your implementation if
	 * the selection contains a {@link ProductTypeID} which is displayed by your implementation,
	 * and if so select it
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection);

	/**
	 * Returns true if the filter can display the selection or not
	 * @param selection the ISelection to check for
	 * @return true if the filter can display the selection or not
	 */
	public boolean canHandleSelection(ISelection selection);
	
	/**
	 * Returns the classes the filter is responsible for.
	 * @return the classes the filter is responsible for
	 */
	Set<Class> getClasses();
}
