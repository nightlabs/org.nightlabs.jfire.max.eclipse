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

package org.nightlabs.jfire.reporting.admin.ui;

import org.eclipse.birt.report.designer.core.model.SessionHandleAdapter;
import org.eclipse.birt.report.designer.ui.extensions.ExceptionHandlerRegistry;
import org.eclipse.birt.report.designer.ui.extensions.IDesignerExceptionHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.admin.ui.platform.ClientResourceLocator;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ReportingAdminPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ReportingAdminPlugin plugin;

	public static final String SCOPE_REPORTING_ADMIN = "reporting.admin"; //$NON-NLS-1$

	public static final String ZONE_REPORTING_ADMIN = ReportingPlugin.class.getName()+"#ZONE_REPORTING_ADMIN"; //$NON-NLS-1$

	public static final String REPORT_USECASE_PREVIEW = "org.nightlabs.jfire.reporting.admin.ui.previewReportUseCase";  //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public ReportingAdminPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		SessionHandleAdapter.getInstance().getSessionHandle().setResourceLocator(new ClientResourceLocator());
		ExceptionHandlerRegistry.getInstance().registerExceptionHandler(new IDesignerExceptionHandler() {
			public void handle(Throwable exception) {
				org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry.asyncHandleException(exception);
			}
		});
//		DesignSession.setBirtResourcePath(RCPUtil.getResourceAsFile(ResourcesPlugin.getWorkspace().getRoot().getProject("ReportLocalisation").getFolder("JFireReportingTrade-Reporting-Invoice-Default-InvoiceLayout")).getAbsoluteFile().toString());
	}

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
	public static ReportingAdminPlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.nightlabs.jfire.reporting.admin.ui", path); //$NON-NLS-1$
	}

	/**
	 * Returns a new ReportManager bean.
	 * @deprecated
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

}
