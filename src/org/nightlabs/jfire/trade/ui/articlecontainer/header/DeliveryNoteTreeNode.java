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

package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DeliveryNoteTreeNode extends HeaderTreeNode
{
	private DeliveryNote deliveryNote;

	/**
	 * @param parent
	 */
	public DeliveryNoteTreeNode(HeaderTreeNode parent, byte position, DeliveryNote deliveryNote)
	{
		super(parent, position);
		this.deliveryNote = deliveryNote;
		init();
	}

	@Override
	public Image getColumnImage(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return getHeaderTreeComposite().imageDeliveryNoteTreeNode;
			default:
				return null;
		}
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return ArticleContainerUtil.getArticleContainerID(deliveryNote);
			default:
				return null;
		}
	}

	@Override
	public boolean hasChildren()
	{
		return false;
	}

	@Override
	@Implement
	protected List<Object> loadChildData(ProgressMonitor monitor)
	{
		return null;
	}

	@Override
	@Implement
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData)
	{
		return null;
	}

	/**
	 * @return Returns the deliveryNote.
	 */
	public DeliveryNote getDeliveryNote()
	{
		return deliveryNote;
	}

}
