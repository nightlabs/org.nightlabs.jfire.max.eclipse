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

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * 
 */
public abstract class AbstractProductTypeQuickListFilter
implements IProductTypeQuickListFilter
{
	private LinkedList<ISelectionChangedListener> selectionChangedListeners = new LinkedList<ISelectionChangedListener>();

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

		SelectionChangedEvent event = new SelectionChangedEvent(
				this, selection);

		for (Iterator it = selectionChangedListeners.iterator(); it.hasNext();) {
			ISelectionChangedListener listener = (ISelectionChangedListener) it.next();
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
	 * At the moment (2007-03-27) this method only deselect everything.
	 *
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
	//TODO Implement setSelection()
//		throw new UnsupportedOperationException("Not implemented!");
	}
}
