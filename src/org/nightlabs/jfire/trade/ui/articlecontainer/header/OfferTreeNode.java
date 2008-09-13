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
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class OfferTreeNode extends HeaderTreeNode
{
	private Offer offer;

	public OfferTreeNode(HeaderTreeNode parent, byte position, Offer offer)
	{
		super(parent, position);
		this.offer = offer;
		init();
	}

	@Override
	public Image getColumnImage(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return getHeaderTreeComposite().imageOfferTreeNode;
			default:
				return null;
		}
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0: return ((offer instanceof RecurringOffer) ? "Recc." : "") +  offer.getOfferIDPrefix() + '/' + ObjectIDUtil.longObjectIDFieldToString(offer.getOfferID());
			default:
				return ""; //$NON-NLS-1$
		}
	}

	/**
	 * This method returns always <tt>false</tt>.
	 *
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#hasChildren()
	 */
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
	 * @return Returns the offer.
	 */
	public Offer getOffer()
	{
		return offer;
	}
}
