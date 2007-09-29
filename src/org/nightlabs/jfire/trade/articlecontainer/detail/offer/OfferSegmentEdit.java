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

package org.nightlabs.jfire.trade.articlecontainer.detail.offer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;

import org.nightlabs.jfire.trade.articlecontainer.detail.AbstractSegmentEdit;
import org.nightlabs.jfire.trade.articlecontainer.detail.ArticleAdder;
import org.nightlabs.jfire.trade.articlecontainer.detail.ArticleEdit;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class OfferSegmentEdit extends AbstractSegmentEdit
{
	private OfferSegmentComposite offerSegmentComposite = null;

	public OfferSegmentEdit()
	{
	}

//	/**
//	 * @see org.nightlabs.jfire.trade.articlecontainer.SegmentEdit#createComposite(org.eclipse.swt.widgets.Composite)
//	 */
//	public Composite createComposite(Composite parent)
//	{
//		offerSegmentComposite = new OrderSegmentComposite(parent, this);
//		return offerSegmentComposite;
//	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.AbstractSegmentEdit#_createComposite(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite _createComposite(Composite parent)
	{
		offerSegmentComposite = new OfferSegmentComposite(parent, this);
		return offerSegmentComposite;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.AbstractSegmentEdit#createArticleAdderComposite(org.nightlabs.jfire.trade.articlecontainer.ArticleAdder)
	 */
	protected void createArticleAdderComposite(ArticleAdder articleAdder)
	{
		if (offerSegmentComposite.articleAdderPlaceholderLabel != null) {
			offerSegmentComposite.articleAdderPlaceholderLabel.dispose();
			offerSegmentComposite.articleAdderPlaceholderLabel = null;
		}
		articleAdder.createComposite(offerSegmentComposite.articleAdderArea);
		offerSegmentComposite.articleAdderArea.getGridLayout().numColumns =
				offerSegmentComposite.articleAdderArea.getChildren().length;
		offerSegmentComposite.layout(true, true);
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.AbstractSegmentEdit#createArticleEditComposite(org.nightlabs.jfire.trade.articlecontainer.ArticleEdit)
	 */
	protected void createArticleEditComposite(ArticleEdit articleEdit)
	{
		articleEdit.createComposite(offerSegmentComposite.articleEditArea);
		offerSegmentComposite.layout(true, true);
	}

	protected void _populateArticleEditContextMenu(IMenuManager manager)
	{
	}
}
