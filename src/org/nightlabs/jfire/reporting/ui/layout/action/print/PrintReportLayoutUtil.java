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

package org.nightlabs.jfire.reporting.ui.layout.action.print;

import java.awt.print.PrinterException;
import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.base.ui.print.PrinterUseCase;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCase;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCaseRegistry;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule.UseCaseConfig;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.layout.RenderedReportLayoutProvider;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.print.DocumentPrinter;
import org.nightlabs.print.PrinterInterface;

/**
 * Util class for printing JFire BIRT reports.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrintReportLayoutUtil {

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(PrintReportLayoutUtil.class);
	
	/**
	 * Creates a {@link RenderReportRequest} out of the given parameters 
	 * and prints using the report use case that can be obtained for the 
	 * type of the given reportRegistryItemID.
	 * 
	 * @throws PrinterException
	 */
	public static void printReportLayout(
			ReportRegistryItemID reportRegistryItemID, 
			Map<String, Object> params, 
			IProgressMonitor monitor
		)
	throws PrinterException
	{
		ReportUseCase reportUseCase = ReportUseCaseRegistry.sharedInstance().getReportUseCaseByLayoutType(reportRegistryItemID.reportRegistryItemType);
		if (reportUseCase == null)
			throw new IllegalStateException("Could not lookup reportUseCase by the reportRegistryItemType: "+reportRegistryItemID.reportRegistryItemType); //$NON-NLS-1$
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(reportUseCase.getId());
		if (useCaseConfig == null)
			throw new IllegalStateException("Could not lookup ReportUseCaseConfig from ConfigModule, reportUseCaseID: "+reportUseCase.getId()); //$NON-NLS-1$
		Birt.OutputFormat format = null;
		if (useCaseConfig.getPrintFormat() != null)
			format = Birt.OutputFormat.valueOf(useCaseConfig.getPrintFormat());
		if (format == null)
			throw new IllegalStateException("Could not lookup (valid) printFormat for the reportUseCaseID: "+reportUseCase.getId()+". The found value was: "+useCaseConfig.getPrintFormat()); //$NON-NLS-1$ //$NON-NLS-2$
		RenderReportRequest renderRequest = new RenderReportRequest();
		renderRequest.setReportRegistryItemID(reportRegistryItemID);
		renderRequest.setParameters(params);
		renderRequest.setOutputFormat(format);
		printReportLayout(renderRequest, reportUseCase.getId(), monitor);
	}

	/**
	 * Takes the given renderRequest and assigns the output format
	 * to it that is configured with the appropriate report use case
	 * for the type of layout and prints via 
	 * {@link #printReportLayout(RenderReportRequest, String, IProgressMonitor)}
	 * 
	 * @throws PrinterException
	 */
	public static void printReportLayoutWithDefaultFormat (
			RenderReportRequest renderRequest,
			String reportUseCaseID,
			IProgressMonitor monitor
		) 
	throws PrinterException 
	{
		RCPUtil.getSaveProgressMonitor(monitor).setTaskName(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil.saveProgressMonitor.lookupReportUseCaseTask.naem")); //$NON-NLS-1$
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(reportUseCaseID);
		if (useCaseConfig == null)
			throw new IllegalStateException("Could not lookup ReportUseCaseConfig from ConfigModule, reportUseCaseID: "+reportUseCaseID); //$NON-NLS-1$
		Birt.OutputFormat format = null;
		if (useCaseConfig.getPrintFormat() != null)
			format = Birt.OutputFormat.valueOf(useCaseConfig.getPrintFormat());
		if (format == null)
			throw new IllegalStateException("Could not lookup (valid) printFormat for the reportUseCaseID: "+reportUseCaseID+". The found value was: "+useCaseConfig.getPrintFormat()); //$NON-NLS-1$ //$NON-NLS-2$
		renderRequest.setOutputFormat(format);
		printReportLayout(renderRequest, reportUseCaseID, monitor);
	}
	
	/**
	 * Fetches the report referenced by the given reportRequest (using {@link RenderedReportLayoutProvider}) 
	 * and prints using the printer configuration assigned to the given report use case.
	 * 
	 * @throws PrinterException
	 */
	public static void printReportLayout(
			RenderReportRequest renderRequest,
//			ReportRegistryItemID reportRegistryItemID, 
//			Map<String, Object> params, 
//			Birt.OutputFormat format, 
			String reportUseCaseID, 
			IProgressMonitor monitor
		) 
	throws PrinterException 
	{
		RCPUtil.getSaveProgressMonitor(monitor).setTaskName(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil.saveProgressMonitor.prepareLayoutTask.name")); //$NON-NLS-1$
		PreparedRenderedReportLayout preparedLayout = RenderedReportLayoutProvider.sharedInstance().getPreparedRenderedReportLayout(renderRequest, monitor);
		RCPUtil.getSaveProgressMonitor(monitor).setTaskName(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil.saveProgressMonitor.lookupPrinterUseCaseTask.name")); //$NON-NLS-1$
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(reportUseCaseID);
		if (useCaseConfig.getPrinterUseCase() != null)
			printFile(preparedLayout.getEntryFile(), useCaseConfig.getPrinterUseCase(), monitor);
		else {
			logger.warn("No printerUseCase was configured for reportUseCase "+reportUseCaseID+", trying to print to defaultPrinterUseCase"); //$NON-NLS-1$ //$NON-NLS-2$
			printFile(preparedLayout.getEntryFile(), PrinterUseCase.DEFAULT_USE_CASE_ID, monitor);
		}
	}
	
	/**
	 * Prints the ready handled report layout whose entryFile was
	 * stored to the given file. It will use the report use case
	 * that can be found for the type of layout of the given reportRegistryItemID.
	 * 
	 * @throws PrinterException
	 */
	public static void printReportLayout(
			ReportRegistryItemID reportRegistryItemID, 
			File file,
			IProgressMonitor monitor
		)
	throws PrinterException
	{
		ReportUseCase reportUseCase = ReportUseCaseRegistry.sharedInstance().getReportUseCaseByLayoutType(reportRegistryItemID.reportRegistryItemType);
		if (reportUseCase != null) {
//			throw new IllegalStateException("Could not lookup reportUseCase by the reportRegistryItemType: "+reportRegistryItemID.reportRegistryItemType);
			ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
			UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(reportUseCase.getId());
			if (useCaseConfig == null)
				throw new IllegalStateException("Could not lookup ReportUseCaseConfig from ConfigModule, reportUseCaseID: "+reportUseCase.getId()); //$NON-NLS-1$

			if (useCaseConfig.getPrinterUseCase() != null) {
				printFile(file, useCaseConfig.getPrinterUseCase(), monitor);
				return;
			}
		}
		logger.warn("No reportUseCase, or configured printer use case was found for "+reportRegistryItemID+" found, trying to print to defaultPrinterUseCase"); //$NON-NLS-1$ //$NON-NLS-2$
		printFile(file, PrinterUseCase.DEFAULT_USE_CASE_ID, monitor);
	}

	/**
	 * Prints the given file using the printer configuration of the
	 * given printerUseCase.
	 * 
	 * @throws PrinterException
	 */
	public static void printFile(File file, String printerUseCaseID, IProgressMonitor monitor) 
	throws PrinterException 
	{
		RCPUtil.getSaveProgressMonitor(monitor).setTaskName(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil.saveProgressMonitor.printTask.name")); //$NON-NLS-1$
		PrinterInterface iFace = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(org.nightlabs.print.PrinterInterfaceManager.INTERFACE_FACTORY_DOCUMENT, printerUseCaseID);
		if (!(iFace instanceof DocumentPrinter))
			throw new PrinterException("Obtained PrinterInterface was no DocumentPrinter but "+((iFace != null) ? iFace.getClass().getName() : "null")); //$NON-NLS-1$ //$NON-NLS-2$
		DocumentPrinter documentPrinter = (DocumentPrinter) iFace;
		documentPrinter.printDocument(file);
	}
}
