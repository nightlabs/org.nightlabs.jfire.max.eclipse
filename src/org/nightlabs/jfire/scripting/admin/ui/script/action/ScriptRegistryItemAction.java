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

package org.nightlabs.jfire.scripting.admin.ui.script.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;

/**
 * Base class for all actions that manipulate <code>ReportRegistryItem</code>s.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class ScriptRegistryItemAction extends Action implements IScriptRegistryItemAction {

	/**
	 *
	 */
	public ScriptRegistryItemAction() {
		super();
	}

	/**
	 * @param text
	 */
	public ScriptRegistryItemAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public ScriptRegistryItemAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public ScriptRegistryItemAction(String text, int style) {
		super(text, style);
	}


	private Collection<ScriptRegistryItem> scriptRegistryItems = new HashSet<ScriptRegistryItem>();

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.action.IScriptRegistryItemAction#setScriptRegistryItems(java.util.Collection)
	 */
	public void setScriptRegistryItems(Collection<ScriptRegistryItem> reportRegistryItem) {
		this.scriptRegistryItems = reportRegistryItem;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.action.IScriptRegistryItemAction#getScriptRegistryItems()
	 */
	public Collection<ScriptRegistryItem> getScriptRegistryItems() {
		return scriptRegistryItems;
	}


	/**
	 * All <code>ScriptParameterSetAction</code>s should do their work
	 * in this method as they can be passed <code>ScriptRegistryItem</code>s
	 * to interact with.
	 *
	 * @param scriptRegistryItems The <code>ScriptRegistryItem</code>s this action was invoked on
	 * @see #setScriptRegistryItems(Collection)
	 */
	public abstract void run(Collection<ScriptRegistryItem> reportRegistryItems);


	/**
	 * Runs the action with {@link #run(ReportRegistryItem)} passing
	 * the current <code>ReportRegistryItem</code>. After the action
	 * was performed the current <code>ReportRegistryItem</code> is
	 * set back to <code>null</code> so {@link #setReportRegistryItem(ReportRegistryItem)}
	 * has to be invoked again before rerunning the action.
	 *
	 */
	@Override
	public void run() {
		run(scriptRegistryItems);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.action.IScriptRegistryItemAction#calculateEnabled(java.util.Collection)
	 */
	public boolean calculateEnabled(Collection<ScriptRegistryItem> registryItems) {
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.script.action.IScriptRegistryItemAction#calculateVisible(java.util.Collection)
	 */
	public boolean calculateVisible(Collection<ScriptRegistryItem> registryItems) {
		return true;
	}



	/**
	 * Get the cached instance fo ScriptRegistryItem if only one
	 * can be found in the passed selection subjects. Expects
	 * <code>ScriptRegistryItemID</code>s as values in the given set.
	 * Returns null if either no or more than one entry is found
	 * in the given set.
	 */
	public ScriptRegistryItem getSingleSelectionRegistryItem(Set<ScriptRegistryItemID> subjects) {
		if (subjects.size() != 1)
			return null;
		ScriptRegistryItemID itemID = subjects.iterator().next();
		// TODO remove NullProgressMonitor
		ScriptRegistryItemProvider.sharedInstance().getScriptRegistryItem(
				itemID, new NullProgressMonitor());
		return null;
	}

}
