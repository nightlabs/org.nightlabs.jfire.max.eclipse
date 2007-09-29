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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class GeneralEditorInputDeliveryNote
extends GeneralEditorInput
implements IEditorInput
{
	private DeliveryNoteID deliveryNoteID;

	public GeneralEditorInputDeliveryNote()
	{
	}

	public GeneralEditorInputDeliveryNote(DeliveryNoteID deliveryNoteID)
	{
		this.deliveryNoteID = deliveryNoteID;
	}

	/**
	 * @return Returns the deliveryNoteID.
	 */
	public DeliveryNoteID getDeliveryNoteID()
	{
		return deliveryNoteID;
	}

	public int hashCode()
	{
		return deliveryNoteID == null ? 0 : deliveryNoteID.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj == this) return true;

		if (!(obj instanceof GeneralEditorInputDeliveryNote))
			return false;

		GeneralEditorInputDeliveryNote other = (GeneralEditorInputDeliveryNote)obj;

		return Util.equals(this.deliveryNoteID, other.deliveryNoteID);
	}

	public String getName()
	{
		return String.format(
				Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.GeneralEditorInputDeliveryNote.name"), //$NON-NLS-1$
				(deliveryNoteID == null ? "" : deliveryNoteID.deliveryNoteIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(deliveryNoteID.deliveryNoteID))); //$NON-NLS-1$
	}

	private static final String IMAGE = "icons/articlecontainer/detail/deliverynote/GeneralEditorInputDeliveryNote.16x16.png"; //$NON-NLS-1$

	public ImageDescriptor getImageDescriptor()
	{
		return TradePlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE);
	}
}
