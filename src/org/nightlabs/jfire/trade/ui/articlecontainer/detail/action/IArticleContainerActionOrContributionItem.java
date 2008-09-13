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

import org.nightlabs.base.ui.action.IUpdateActionOrContributionItem;

public interface IArticleContainerActionOrContributionItem
extends IUpdateActionOrContributionItem
{
	/**
	 * This method is called after creation.
	 *
	 * @param articleEditActionRegistry The registry that is managing this <code>IArticleEditAction</code>.
	 */
	void init(
			ArticleContainerActionRegistry articleContainerActionRegistry);

	/**
	 * @return Returns the registry that is managing this <code>IArticleContainerAction</code> and
	 *		was passed to {@link #init(ArticleContainerActionRegistry)} before.
	 */
//	ArticleContainerActionRegistry getArticleContainerActionRegistry();

//	boolean calculateVisible();
//	boolean calculateEnabled();
}
