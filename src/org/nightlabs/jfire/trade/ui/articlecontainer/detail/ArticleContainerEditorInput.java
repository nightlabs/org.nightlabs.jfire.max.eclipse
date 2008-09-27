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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.util.Util;


/**
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerEditorInput
implements IEditorInput
{
	private ArticleContainerID articleContainerID;
	
	public ArticleContainerEditorInput(ArticleContainerID articleContainerID)
	{
		this.articleContainerID = articleContainerID;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists()
	{
		return true;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable()
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText()
	{
		// TODO this needs to be implemented correctly and display some useful info about the articleContainer
		return "It needs a title tooltip to work!"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	public ArticleContainerID getArticleContainerID() {
		return articleContainerID;
	}

	private Class<?> articleContainerClass = null;
	
	public Class<?> getArticleContainerClass() {
		if (articleContainerClass == null) {
			articleContainerClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(getArticleContainerID());
		}
		return articleContainerClass;
	}
	
	@Override
	public String getName()
	{
		return 
			TradePlugin.getArticleContainerTypeString(getArticleContainerClass(), true) + " " + 
			ArticleContainerUtil.getArticleContainerID(getArticleContainerID());
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return TradePlugin.getArticleContainerImageDescriptor(getArticleContainerClass());
	}
	
	@Override
	public int hashCode()
	{
		return articleContainerID == null ? 0 : articleContainerID.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;

		if (!(obj instanceof ArticleContainerEditorInput))
			return false;

		ArticleContainerEditorInput other = (ArticleContainerEditorInput)obj;

		return Util.equals(this.articleContainerID, other.articleContainerID);
	}
	
	
}
