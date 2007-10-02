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
package org.nightlabs.jfire.scripting.editor2d.ui;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScriptingEditor2DPlugin 
extends AbstractUIPlugin 
{
	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.jfire.scripting.editor2d.ui"; //$NON-NLS-1$

	// The shared instance
	private static ScriptingEditor2DPlugin plugin;
	
	private static ResourceBundle resourceBundle;
	
	/**
	 * The constructor
	 */
	public ScriptingEditor2DPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		resourceBundle = Platform.getResourceBundle(getBundle());		
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ScriptingEditor2DPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns a string from the resource bundle, knowing its key. Note: the generated
	 * code is not strong enough because a NullPointerException is raised if there is no
	 * resource bundle, instead of 'key' to be returned for consistency.
	 * 
	 * @return The string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle. Note that for this plugin, the resource
	 * bundle is the same as the plugin descriptor resource bundle.
	 * 
	 * @return The plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}	
}
