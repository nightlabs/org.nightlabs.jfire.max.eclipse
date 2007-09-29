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

package org.nightlabs.jfire.trade.admin.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TradeAdminPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static TradeAdminPlugin plugin;
//	//Resource bundle.
//	private static ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public TradeAdminPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		resourceBundle = Platform.getResourceBundle(getBundle());	
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
//		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TradeAdminPlugin getDefault() {
		return plugin;
	}

//	/**
//	 * Returns the string from the plugin's resource bundle,
//	 * or 'key' if not found.
//	 */
//	public static String getResourceString(String key) {
//		try {
//			return resourceBundle.getString(key);
//		} catch (Exception e) {
//			return key;
//		}		
//	}
//
//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle() {
//		return resourceBundle;
//	}
	
//	/**
//	 * Returns the string from the plugin's resource bundle,
//	 * or 'key' if not found.
//	 */
//	public static String getResourceString(String key) {
//		ResourceBundle bundle = TradeAdminPlugin.getDefault().getResourceBundle();
//		try {
//			return (bundle != null) ? bundle.getString(key) : key;
//		} catch (MissingResourceException e) {
//			return key;
//		}
//	}
//
//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle() {
//		try {
//			if (resourceBundle == null)
//				resourceBundle = ResourceBundle.getBundle("org.nightlabs.jfire.trade.admin.ui.plugin");
//		} catch (MissingResourceException x) {
//			resourceBundle = null;
//		}
//		return resourceBundle;
//	}
}
