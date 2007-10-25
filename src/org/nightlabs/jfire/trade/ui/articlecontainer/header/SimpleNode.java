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

import org.nightlabs.progress.ProgressMonitor;

public class SimpleNode extends HeaderTreeNode
{
	private String text;
	private boolean hasChildren;

	public SimpleNode(HeaderTreeNode parent, byte position, String text, boolean hasChildren)
	{
		super(parent, position);
		this.text = text;
		this.hasChildren = hasChildren;
		init();
	}

	@Override
	public boolean hasChildren()
	{
		return hasChildren;
	}

	@Override
	public String getColumnText(int columnIndex)
	{
		return text;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#loadChildData(ProgressMonitor)
	 */
	@Override
	protected List loadChildData(ProgressMonitor monitor)
	{
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#createChildNodes(java.util.List)
	 */
	@Override
	protected List<HeaderTreeNode> createChildNodes(List childData)
	{
		return null;
	}

}
