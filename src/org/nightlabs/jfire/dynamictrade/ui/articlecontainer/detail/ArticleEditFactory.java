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

package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleProductTypeClassGroup;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleEditFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;

/**
 * This implementation of {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEditFactory}
 * creates one instance of {@link org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleEdit}
 * to allow manipulation of articles which
 * wrap {@link org.nightlabs.jfire.dynamictrade.ui.store.DynamicProductType}s.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleEditFactory extends AbstractArticleEditFactory
{

	public Collection<? extends org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit> createArticleEdits(SegmentEdit segmentEdit,
			ArticleProductTypeClassGroup articleProductTypeClassGroup, Collection<? extends ArticleCarrier> articleCarriers)
	{
		ArrayList<org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit> res = new ArrayList<org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit>(1);
		ArticleEdit edit = new ArticleEdit();
		edit.init(this, segmentEdit, articleProductTypeClassGroup, new HashSet<ArticleCarrier>(articleCarriers));
		res.add(edit);
		return res;
	}

}
