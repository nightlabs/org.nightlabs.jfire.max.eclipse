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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerRegistry;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportViewPrintConfigModule extends ConfigModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static class UseCaseConfig implements Cloneable, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String reportUseCaseID;
		
		private String viewerFormat;
		private String reportViewerID;
		private String printFormat;
		private String printerUseCase;

		/**
		 * @deprecated Only for Serialization
		 */
		public UseCaseConfig() {
		}
		
		public UseCaseConfig(String reportUseCaseID) {
			this.reportUseCaseID = reportUseCaseID;
		}
		
		public String getReportUseCaseID() {
			return reportUseCaseID;
		}
		
		/**
		 * @return the printFormat
		 */
		public String getPrintFormat() {
			return printFormat;
		}
		/**
		 * @param printFormat the printFormat to set
		 */
		public void setPrintFormat(String printFormat) {
			this.printFormat = printFormat;
		}
		/**
		 * @return the reportViewerID
		 */
		public String getReportViewerID() {
			return reportViewerID;
		}
		/**
		 * @param reportViewerID the reportViewerID to set
		 */
		public void setReportViewerID(String reportViewerID) {
			this.reportViewerID = reportViewerID;
		}
		/**
		 * @return the viewerFormat
		 */
		public String getViewerFormat() {
			return viewerFormat;
		}
		/**
		 * @param viewerFormat the viewerFormat to set
		 */
		public void setViewerFormat(String viewerFormat) {
			this.viewerFormat = viewerFormat;
		}		
		
		/**
		 * @return the printerUseCase
		 */
		public String getPrinterUseCase() {
			return printerUseCase;
		}
		
		/**
		 * @param printerUseCase the printerUseCase to set
		 */
		public void setPrinterUseCase(String printerUseCase) {
			this.printerUseCase = printerUseCase;
		}
		
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("This should never happen", e); //$NON-NLS-1$
			}
		}
		
	}

	private boolean useSameForAll;
	private Map<String, UseCaseConfig> reportUseCaseConfigs;
	
	/**
	 * 
	 */
	public ReportViewPrintConfigModule() {
	}
	
	@Override
	public void init() throws InitException {
		super.init();
		if (reportUseCaseConfigs == null) {
			setUseSameForAll(true);
			reportUseCaseConfigs = new HashMap<String, UseCaseConfig>();
			UseCaseConfig useCaseConfig = new UseCaseConfig(null);
//			useCaseConfig.setViewerFormat(Birt.OutputFormat.pdf.toString());
			useCaseConfig.setViewerFormat(Birt.OutputFormat.html.toString());
			useCaseConfig.setReportViewerID(ReportViewerRegistry.DEFAULT_REPORT_VIEWER_ID);
			useCaseConfig.setPrintFormat(Birt.OutputFormat.pdf.toString());
			Collection<ReportUseCase> useCases = ReportUseCaseRegistry.sharedInstance().getReportUseCases();
			for (ReportUseCase useCase : useCases) {
				useCaseConfig.setPrinterUseCase(useCase.getDefaultPrinterUseCase());
				reportUseCaseConfigs.put(useCase.getId(), (UseCaseConfig)useCaseConfig.clone());
			}
		}
	}

	/**
	 * @return the reportUseCaseConfigs
	 */
	public Map<String, UseCaseConfig> getReportUseCaseConfigs() {
		return reportUseCaseConfigs;
	}

	/**
	 * @param reportUseCaseConfigs the reportUseCaseConfigs to set
	 */
	public void setReportUseCaseConfigs(
			Map<String, UseCaseConfig> reportUseCaseConfigs) {
		this.reportUseCaseConfigs = reportUseCaseConfigs;
		setChanged();
	}

	/**
	 * @return the useSameForAll
	 */
	public boolean isUseSameForAll() {
		return useSameForAll;
	}

	/**
	 * @param useSameForAll the useSameForAll to set
	 */
	public void setUseSameForAll(boolean useSameForAll) {
		this.useSameForAll = useSameForAll;
		setChanged();
	}
	
	/**
	 * Returns and layzily creates the {@link ReportViewPrintConfigModule} with
	 * the help of the static {@link Config} instance.
	 */
	public static ReportViewPrintConfigModule sharedInstance() {
		try {
			return (ReportViewPrintConfigModule)Config.sharedInstance().createConfigModule(ReportViewPrintConfigModule.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
}
