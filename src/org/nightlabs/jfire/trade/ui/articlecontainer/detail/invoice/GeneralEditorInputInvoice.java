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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class GeneralEditorInputInvoice
extends GeneralEditorInput
implements IEditorInput
{
	private InvoiceID invoiceID;

	public GeneralEditorInputInvoice()
	{
	}

	public GeneralEditorInputInvoice(InvoiceID invoiceID)
	{
		this.invoiceID = invoiceID;
	}

	/**
	 * @return Returns the invoiceID.
	 */
	public InvoiceID getInvoiceID()
	{
		return invoiceID;
	}

	public int hashCode()
	{
		return invoiceID == null ? 0 : invoiceID.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj == this) return true;

		if (!(obj instanceof GeneralEditorInputInvoice))
			return false;

		GeneralEditorInputInvoice other = (GeneralEditorInputInvoice)obj;

		return Util.equals(this.invoiceID, other.invoiceID);
	}

	public String getName()
	{
		return String.format(
				Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.GeneralEditorInputInvoice.name"), //$NON-NLS-1$
				(invoiceID == null ? "" : invoiceID.invoiceIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(invoiceID.invoiceID))); //$NON-NLS-1$
	}

	private static final String IMAGE = "icons/articlecontainer/detail/invoice/GeneralEditorInputInvoice.16x16.png"; //$NON-NLS-1$

	public ImageDescriptor getImageDescriptor()
	{
		return TradePlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE);
	}
}
