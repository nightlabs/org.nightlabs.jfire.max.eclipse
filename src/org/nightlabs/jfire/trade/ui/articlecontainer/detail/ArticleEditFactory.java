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

import java.util.Collection;

import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleProductTypeClassGroup;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ArticleEditFactory extends SegmentTypeProductTypeDependentFactory
{
	/**
	 * This method should create a <tt>Collection</tt> of all necessary {@link ArticleEdit}s
	 * so that all {@link Article}s of the given <tt>Collection</tt> of {@link org.nightlabs.jfire.trade.ui.Article}s
	 * were accepted by/added to one. 
	 * <p>
	 * You must call {@link ArticleEdit#init(List, IWorkbenchPartSite, ArticleProductTypeClassGroup, Set)} in
	 * your implementation of this method!
	 *
	 * @param segmentEdit The <tt>SegmentEdit</tt> into which this ArticleEditFactory should create the <tt>ArticleEdit</tt>s.
	 * @param articleProductTypeClassGroup The group to which all carriers must belong. This is passed, because the subset might be empty.
	 * @param articleCarriers The subset of {@link ArticleCarrier}s for which to create <tt>ArticleEdit</tt>s.
	 *		Because this method might be called when adding <tt>Article</tt>s afterwards,
	 *		this is not identical with <tt>articleProductTypeGroup</tt>!
	 * @return a <tt>Collection</tt> of {@link ArticleEdit}
	 */
	Collection<? extends ArticleEdit> createArticleEdits(
			SegmentEdit segmentEdit, ArticleProductTypeClassGroup articleProductTypeClassGroup,
			Collection<? extends ArticleCarrier> articleCarriers);

	/**
	 * This method adds an <code>IArticleEditActionDelegate</code>, which is registered by
	 * its <code>articleEditActionID</code> in a map. It must be retrievable by
	 * {@link #getArticleEditActionDelegate(String)} afterwards.
	 *
	 * @param delegate The delegate to add.
	 *
	 * @throws IllegalStateException if the <code>articleEditActionID</code>
	 * has already been registered before.
	 */
	void addArticleEditActionDelegate(IArticleEditActionDelegate delegate);

	/**
	 * This method looks up the <code>IArticleEditActionDelegate</code> by its
	 * <code>articleEditActionID</code>.
	 *
	 * @param articleEditActionID The id to search for.
	 * @return Returns either <code>null</code>, if there is no <code>IArticleEditActionDelegate</code>
	 *		registered for the given id or the matching delegate that has been passed to
	 *		{@link #addArticleEditActionDelegate(IArticleEditActionDelegate)} before.
	 */
	IArticleEditActionDelegate getArticleEditActionDelegate(String articleEditActionID);
}
