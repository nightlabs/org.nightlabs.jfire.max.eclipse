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
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.GeneralEditorInputDeliveryNote;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.GeneralEditorInputInvoice;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.GeneralEditorInputOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.GeneralEditorInputOrder;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class GeneralEditorInput
implements IEditorInput
{
	private String segmentContext;

	public GeneralEditorInput()
	{
		if (this instanceof GeneralEditorInputOrder)
			segmentContext = SegmentEditFactory.SEGMENTCONTEXT_ORDER;
		else if (this instanceof GeneralEditorInputOffer)
			segmentContext = SegmentEditFactory.SEGMENTCONTEXT_OFFER;
		else if (this instanceof GeneralEditorInputInvoice)
			segmentContext = SegmentEditFactory.SEGMENTCONTEXT_INVOICE;
		else if (this instanceof GeneralEditorInputDeliveryNote)
			segmentContext = SegmentEditFactory.SEGMENTCONTEXT_DELIVERY_NOTE;
		else
			throw new UnsupportedOperationException("This class is not a supported child of " + GeneralEditorInput.class.getName()); //$NON-NLS-1$
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
//		return segmentContext;
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
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * @return Returns the segmentContext.
	 */
	public String getSegmentContext()
	{
		return segmentContext;
	}
}
