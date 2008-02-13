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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.Set;

import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface IArticleEditActionDelegate
{
	/**
	 * This method is called after creation.
	 *
	 * @param articleEditActionID The id of the articleEditAction for which this delegate has been created.
	 * @param name The name as defined in the extension. This is only for the developer and never shown to the user.
	 *		It might be <code>null</code> (if undefined in the extension).
	 */
	void init(String articleEditActionID, String name);

	/**
	 * @return Returns the action that has been passed to {@link #init(String, String)} before. This
	 *		cannot be <code>null</code> after initialization.
	 */
	String getArticleEditActionID();

	/**
	 * @return Returns the name that was passed to {@link #init(String, String)}. This might be <code>null</code>!
	 */
	String getName();

	/**
	 * This method is called on the SWT GUI thread whenever the selection of
	 * {@link org.nightlabs.jfire.trade.ui.Article}s changes and
	 * the associated {@link ArticleEdit} has at least one selected <code>Article</code>.
	 * In your implementation, you must check, whether it is possible to apply this action to all the
	 * selected <code>Article</code>s returned by {@link ArticleSelection#getSelectedArticles()}.
	 * <p>
	 * Note, that this method is called on all delegates of a certain <code>articleEditAction</code>, for
	 * which {@link org.nightlabs.jfire.trade.ui.Article}s are selected (the delegates of those
	 * <code>ArticleEdit</code>s that have no selected articles will not be asked). The
	 * action will only be enabled, if all relevant delegates return <code>true</code> in this method.
	 * </p>
	 *
	 * @param articleSelection The local selection of the associated {@link ArticleEdit}.
	 * @param articleSelections In case you need to know what other {@link ArticleSelection}s exist, they're passed here (but you really seldom need them).
	 * @return Returns <code>true</code> if this action is applicable to all selected <code>Article</code>s;
	 *		<code>false</code> if it cannot be applied to at least one selection item.
	 */
	boolean calculateEnabled(ArticleSelection articleSelection, Set<ArticleSelection> articleSelections);

	/**
	 * This method is called on the SWT GUI thread whenever the action
	 * (in the pulldown menu, the toolbar or the context menu)
	 * has been triggered by the user.
	 * <p>
	 * Note that this method is called for all {@link ArticleEdit}s that have
	 * selected Articles.
	 * </p>
	 * @param articleEditAction TODO
	 * @param articleSelection The <code>ArticleSelection</code> on which the action shall
	 *		be applied.
	 */
	void run(IArticleEditAction articleEditAction, ArticleSelection articleSelection);
}
