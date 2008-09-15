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

package org.nightlabs.jfire.reporting.ui.config;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.config.ConfigModule;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DefaultReportViewerCfMod extends ConfigModule {

	private static final long serialVersionUID = 20080915L;

	private boolean useInternalBrowserForPDFs = false;

	/**
	 *
	 */
	public DefaultReportViewerCfMod() {
	}

	/**
	 * @return the useInternalBrowserForPDFs
	 */
	public boolean isUseInternalBrowserForPDFs() {
		return useInternalBrowserForPDFs;
	}

	/**
	 * @param useInternalBrowserForPDFs the useInternalBrowserForPDFs to set
	 */
	public void setUseInternalBrowserForPDFs(boolean useAcrobatJavaBeanForPDFs) {
		this.useInternalBrowserForPDFs = useAcrobatJavaBeanForPDFs;
		setChanged();
	}

	/**
	 * Returns and layzily creates an instance of {@link DefaultReportViewerCfMod}
	 * for the shared instance of {@link Config}
	 */
	public static DefaultReportViewerCfMod sharedInstance() {
		try {
			return Config.sharedInstance().createConfigModule(DefaultReportViewerCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}

}
