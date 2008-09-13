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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractSegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DeliveryNoteSegmentEdit extends AbstractSegmentEdit
{
	private DeliveryNoteSegmentComposite deliveryNoteSegmentComposite = null;

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractSegmentEdit#_createComposite(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Composite _createComposite(Composite parent)
	{
		deliveryNoteSegmentComposite = new DeliveryNoteSegmentComposite(parent, this);
		return deliveryNoteSegmentComposite;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractSegmentEdit#createArticleAdderComposite(org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder)
	 */
	@Override
	protected void createArticleAdderComposite(ArticleAdder articleAdder)
	{
		// we don't have an ArticleAdder in a DeliveryNote (articles are added via the Offer or Order)
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractSegmentEdit#createArticleEditComposite(org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit)
	 */
	@Override
	protected void createArticleEditComposite(ArticleEdit articleEdit)
	{
		articleEdit.createComposite(deliveryNoteSegmentComposite.articleEditArea);
		deliveryNoteSegmentComposite.layout(true, true);
	}

	protected void _populateArticleEditContextMenu(IMenuManager manager)
	{
	}

}
