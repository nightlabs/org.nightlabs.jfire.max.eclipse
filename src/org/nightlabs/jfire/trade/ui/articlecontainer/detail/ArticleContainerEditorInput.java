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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.ArticleContainerEditorInputDeliveryNote;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.ArticleContainerEditorInputReceptionNote;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.ArticleContainerInputInvoice;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.ArticleContainerEditorInputOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.ArticleContainerEditorInputOrder;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class ArticleContainerEditorInput
implements IEditorInput
{
	private String articleContainerClass;

	public ArticleContainerEditorInput()
	{
		if (this instanceof ArticleContainerEditorInputOrder)
			articleContainerClass = Order.class.getName();
		else if (this instanceof ArticleContainerEditorInputOffer)
			articleContainerClass = Offer.class.getName();
		else if (this instanceof ArticleContainerInputInvoice)
			articleContainerClass = Invoice.class.getName();
		else if (this instanceof ArticleContainerEditorInputDeliveryNote)
			articleContainerClass = DeliveryNote.class.getName();
		else if (this instanceof ArticleContainerEditorInputReceptionNote)
			articleContainerClass = ReceptionNote.class.getName();
		else
			throw new UnsupportedOperationException("This class is not a supported child of " + ArticleContainerEditorInput.class.getName()); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists()
	{
		return true;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

//	/**
//	 * @see org.eclipse.ui.IEditorInput#getName()
//	 */
//	public String getName()
//	{
//		return articleContainerClass;
//	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable()
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText()
	{
		// TODO this needs to be implemented correctly and display some useful info about the articleContainer
		return "It needs a title tooltip to work!"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * @return Returns the articleContainerClass.
	 */
	public String getArticleContainerClass()
	{
		return articleContainerClass;
	}

	public abstract ArticleContainerID getArticleContainerID();
}
