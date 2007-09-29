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

public interface IArticleEditActionOrContributionItem
{
	/**
	 * This method is called after creation.
	 *
	 * @param articleEditActionRegistry The registry that is managing this <code>IArticleEditAction</code>.
	 */
	void init(
			ArticleEditActionRegistry articleEditActionRegistry);

	/**
	 * @return Returns the registry that is managing this <code>IArticleEditAction</code> and
	 *		was passed to {@link #init(ArticleEditActionRegistry)} before.
	 */
	ArticleEditActionRegistry getArticleEditActionRegistry();

	/**
	 * This method is called by {@link GeneralEditorActionBarContributor#contributeActions()} and the result
	 * then stored via {@link org.nightlabs.base.ui.action.registry.ActionDescriptor#setVisible(boolean)}.
	 * <p>
	 * An <code>IArticleEditAction</code> can operate in multiple ways: Either
	 * the Action has all the logic (which is independent from knowledge
	 * about specialized <code>Article</code>s and <code>ProductType</code>s) or
	 * the logic is implemented in the delegates. Of course, this can be combined, too.
	 * </p>
	 * <p>
	 * As soon as the logic is not completely within the action, a delegate is
	 * necessary and therefore the action cannot be applied if a delegate is missing
	 * for an ArticleEdit with selected Articles (unselected Articles are generally
	 * ignored).
	 * </p>
	 * <p>
	 * Because the default implementation ({@link ArticleEditAction}) assumes to need delegates,
	 * it is working in the following way: If at least
	 * one <code>ArticleEdit</code> in the active <code>SegmentEdit</code> has a delegate,
	 * then this action is visible - otherwise not.
	 * </p>
	 * <p>
	 * Note, that this method is <b>not</b> called, if the current <code>SegmentEdit</code>
	 * does not contain any <code>ArticleEdit</code> (i.e. is empty) or if there is no current
	 * <code>SegmentEdit</code> at all.
	 * </p>
	 *
	 * @return Returns whether this action shall be visible.
	 */
	boolean calculateVisible();

	/**
	 * As described in the documentation for {@link #calculateVisible()}, the default
	 * implementation ({@link ArticleEditAction}) assumes to need delegates. Hence,
	 * this method returns <code>true</code> if all involved <code>ArticleEdit</code>s
	 * (see {@link ArticleSelection#getArticleEdit()}) do have a delegate for this action
	 * and the method {@link IArticleEditActionDelegate#calculateEnabled(ArticleSelection, Set)}
	 * returns <code>true</code>.
	 *
	 * @param articleSelections A <code>Set</code> of {@link ArticleSelection}.
	 * @return Returns whether this action shall be enabled.
	 */
	boolean calculateEnabled(Set<ArticleSelection> articleSelections);
}
