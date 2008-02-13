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

package org.nightlabs.jfire.scripting.admin.ui.parameter.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;

/**
 * Base class for all actions that manipulate <code>ReportRegistryItem</code>s.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class ScriptParameterSetAction extends Action implements IScriptParameterSetAction {

	/**
	 * 
	 */
	public ScriptParameterSetAction() {
		super();
	}

	/**
	 * @param text
	 */
	public ScriptParameterSetAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public ScriptParameterSetAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public ScriptParameterSetAction(String text, int style) {
		super(text, style);
	}
	
	
	private Collection<ScriptParameterSet> scriptParameterSets = new HashSet<ScriptParameterSet>();

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.parameter.action.IScriptParameterSetAction#setScriptParameterSets(java.util.Collection)
	 */
	public void setScriptParameterSets(Collection<ScriptParameterSet> scriptParameterSets) {
		this.scriptParameterSets = scriptParameterSets;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.parameter.action.IScriptParameterSetAction#getScriptParameterSets()
	 */
	public Collection<ScriptParameterSet> getScriptParameterSets() {
		return scriptParameterSets;
	}

	
	/**
	 * All <code>ScriptParameterSetAction</code>s should do their work
	 * in this method as they can be passed <code>ScriptParameterSet</code>s
	 * to interact with.
	 * 
	 * @param scriptParameterSets The <code>ScriptParameterSet</code>s this action was invoked on
	 * @see #setScriptParameterSets(Collection)
	 */
	public abstract void run(Collection<ScriptParameterSet> scriptParameterSets);
	

	/**
	 * Runs the action with {@link #run(Collection)} passing
	 * the current <code>ScriptParameterSet</code>s. After the action
	 * was performed the current <code>ReportRegistryItem</code>
	 */
	@Override
	public void run() {
		run(scriptParameterSets);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.parameter.action.IScriptParameterSetAction#calculateEnabled(java.util.Collection)
	 */
	public boolean calculateEnabled(Collection<ScriptParameterSet> scriptParameterSets) {
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.ui.parameter.action.IScriptParameterSetAction#calculateVisible(java.util.Collection)
	 */
	public boolean calculateVisible(Collection<ScriptParameterSet> scriptParameterSets) {
		return true;
	}

	
	
	/**
	 * Get the cached instance fo ScriptRegistryItem if only one
	 * can be found in the passed selection subjects. Expects
	 * <code>ScriptRegistryItemID</code>s as values in the given set.
	 * Returns null if either no or more than one entry is found
	 * in the given set.
	 */
	public ScriptRegistryItem getSingleSelectionRegistryItem(Set subjects) {
		if (subjects.size() != 1)
			return null;
		Object o = subjects.iterator().next();
		if (o instanceof ScriptRegistryItemID)
			// TODO remove NullProgressMonitor
			ScriptRegistryItemProvider.sharedInstance().getScriptRegistryItem(
					(ScriptRegistryItemID)o, new NullProgressMonitor());
		return null;
	}
	
}
