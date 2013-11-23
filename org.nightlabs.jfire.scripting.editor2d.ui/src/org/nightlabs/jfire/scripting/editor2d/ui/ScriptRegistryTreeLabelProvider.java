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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.jfire.scripting.editor2d.ui;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptCategory;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemNode;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree;
import org.nightlabs.jfire.scripting.ui.ScriptingPlugin;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptRegistryTreeLabelProvider
extends TableLabelProvider
{
	public ScriptRegistryTreeLabelProvider(Map<ScriptRegistryItemID, Object> scriptRegistryItemID2Result)
	{
		super();
		this.scriptRegistryItemID2Result = scriptRegistryItemID2Result;
	}

	protected Map<ScriptRegistryItemID, Object> scriptRegistryItemID2Result = null;

	public String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof ScriptRegistryItemNode && columnIndex == 1) {
			ScriptRegistryItemNode node = (ScriptRegistryItemNode) element;
			Object o = scriptRegistryItemID2Result.get(node.getRegistryItemID());
			if (o == null)
				return ""; //$NON-NLS-1$
			else
				return String.valueOf(o);
		}

		if (element instanceof ScriptRegistryItemNode && columnIndex == 0)
			return ((ScriptRegistryItemNode)element).getName();

		if (columnIndex == 0)
			return String.valueOf(element);

		return ""; //$NON-NLS-1$
	}

	@Override
	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex != 0)
			return null;
		if (element instanceof ScriptRegistryItemNode) {
			ScriptRegistryItemNode node = (ScriptRegistryItemNode)element;
			if (node.getRegistryItem() == null)
				return null;
			Class<?> clazz = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(node.getRegistryItemID());
			if (clazz == null)
				return null;
			if (clazz.equals(ScriptCategory.class))
				return SharedImages.getSharedImage(ScriptingPlugin.getDefault(), ScriptRegistryItemTree.class, "category"); //$NON-NLS-1$
			else if (clazz.equals(Script.class))
				return SharedImages.getSharedImage(ScriptingPlugin.getDefault(), ScriptRegistryItemTree.class, "script"); //$NON-NLS-1$
		}
		return super.getColumnImage(element, columnIndex);
	}

	@Override
	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}
}
