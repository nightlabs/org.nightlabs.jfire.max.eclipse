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

package org.nightlabs.jfire.reporting.ui;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.app.AbstractApplication;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.parameter.ReportParameterManagerRemote;
import org.nightlabs.util.CacheDirTag;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ReportingPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ReportingPlugin plugin;

	public static final String PLUGIN_ID = ReportingPlugin.class.getPackage().getName();

	public static final String SCOPE_REPORTING = "reporting"; //$NON-NLS-1$

	public static final String ZONE_REPORTING = ReportingPlugin.class.getName()+"#ZONE_REPORTING"; //$NON-NLS-1$

	public static final String DEFAULT_REPORT_USE_CASE_ID = "org.nightlabs.jfire.reporting.ui.defaultReportUseCase"; //$NON-NLS-1$

	//Resource bundle.
//	private ResourceBundle resourceBundle;


	/**
	 * The constructor.
	 */
	public ReportingPlugin() {
		plugin = this;
	}

//	/**
//	 * This method is called upon plug-in activation
//	 */
//	public void start(BundleContext context) throws Exception {
//		super.start(context);
//		try {
//			resourceBundle = Platform.getResourceBundle(getBundle());
//		} catch (MissingResourceException x) {
//			resourceBundle = null;
//		}
//	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ReportingPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.nightlabs.jfire.reporting.ui", path); //$NON-NLS-1$
	}

//	/**
//	 * Returns the string from the plugin's resource bundle,
//	 * or 'key' if not found.
//	 */
//	public static String getResourceString(String key) {
//		ResourceBundle bundle = ReportingPlugin.getDefault().getResourceBundle();
//		try {
//			return (bundle != null) ? bundle.getString(key) : key;
//		} catch (MissingResourceException e) {
//			return key;
//		}
//	}

//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle() {
//		return resourceBundle;
//	}

	/**
	 * Returns a new ReportManager bean.
	 */
	@Deprecated
	public static ReportManagerRemote getReportManager() {
		try {
			return JFireEjb3Factory.getRemoteBean(ReportManagerRemote.class,
					Login.getLogin().getInitialContextProperties()
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a new ReportParameterManager bean.
	 */
	@Deprecated
	public static ReportParameterManagerRemote getReportParameterManager() {
		try {
			return JFireEjb3Factory.getRemoteBean(ReportParameterManagerRemote.class,
					Login.getLogin().getInitialContextProperties()
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static File createReportTempFolder() {
		// TODO: need to delete report temp folder on app start or stop
		File pathFile = new File(AbstractApplication.getRootDir()+File.separator+"report_tmp"); //$NON-NLS-1$
		if (!pathFile.exists())
			pathFile.mkdirs();

		try {
			CacheDirTag cdt = new CacheDirTag(pathFile);
			cdt.tag("JFire.org - org.nightlabs.jfire.reporting.ui", true, false); //$NON-NLS-1$
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		return pathFile;
	}

}
