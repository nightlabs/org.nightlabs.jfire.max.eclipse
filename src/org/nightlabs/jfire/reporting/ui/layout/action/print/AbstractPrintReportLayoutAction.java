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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCase;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCaseRegistry;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard.WizardResult;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractPrintReportLayoutAction extends ReportRegistryItemAction {

	/**
	 * 
	 */
	public AbstractPrintReportLayoutAction() {
	}

	/**
	 * @param text
	 */
	public AbstractPrintReportLayoutAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public AbstractPrintReportLayoutAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AbstractPrintReportLayoutAction(String text, int style) {
		super(text, style);
	}

	protected Collection<ReportRegistryItemID> extractReportLayouts(Collection<ReportRegistryItem> reportRegistryItems) {
		Collection<ReportRegistryItemID> itemIDs = new ArrayList<ReportRegistryItemID>();
		for (ReportRegistryItem item : reportRegistryItems) {
			ReportRegistryItemID id = (ReportRegistryItemID)JDOHelper.getObjectId(item);
			if (ReportLayout.class.isAssignableFrom(JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(id))) {
				itemIDs.add(id);
			}
		}
		return itemIDs;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction#run(java.util.Collection)
	 */
	@Override
	public void run(Collection<ReportRegistryItem> reportRegistryItems) {
		runWithRegistryItemIDs(extractReportLayouts(reportRegistryItems));
	}

	private Map<String, Object> nextRunParams;
	
	protected abstract String getReportUseCaseID();
	
	public void runWithRegistryItemIDs(final Collection<ReportRegistryItemID> reportRegistryItems) {
		Job printJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.print.AbstractPrintReportLayoutAction.printJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.print.AbstractPrintReportLayoutAction.printJob.beginPrintingTask.name"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
				Map<String, Object> params = null;
				boolean paramsSet = false;
				if (nextRunParams != null) {
					params = nextRunParams;
					paramsSet = true;
					nextRunParams = null;
				};
				String errorMessages = ""; //$NON-NLS-1$
				
				for (ReportRegistryItemID itemID : reportRegistryItems) {
					if (params == null && !paramsSet) {
						// if no parameters set by now, get them from the user
						WizardResult dialogResult = ReportParameterWizard.openResult(itemID, false);
						if (!dialogResult.isAcquisitionFinished())
							return Status.OK_STATUS;
						params = dialogResult.getParameters();
						paramsSet = true;
					}
					try {
						printWithParams(itemID, params, monitor);
					} catch (PrinterException e) {
						errorMessages = addErrMessage(errorMessages, "Printing failed for "+itemID+": "+e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					}
				}
				if (!"".equals(errorMessages)) //$NON-NLS-1$
					// TODO: Maybe throw typed exception wrapped in RuntimeException
					throw new IllegalStateException(errorMessages);
				return Status.OK_STATUS;
			}
		};
		printJob.schedule();
	}
	
	public void printWithParams(ReportRegistryItemID registryItemID, Map<String, Object> params, ProgressMonitor monitor)
	throws PrinterException
	{
		String useCaseID = getReportUseCaseID();
		if (useCaseID == null) {
			// Try to lookup the UseCase by the reportLayoutType
			ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCaseByLayoutType(registryItemID.reportRegistryItemType);
			if (useCase != null)
				useCaseID = useCase.getId();
		}
		if (useCaseID == null) {
			// if no usecase found till now, try to find the default/fallback one
			ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCase(ReportingPlugin.DEFAULT_REPORT_USE_CASE_ID);
			if (useCase == null) {
				throw new PrinterException("No useCaseID could be found. A usecase for the type "+registryItemID.reportRegistryItemType+" could not be found and also the default/fallback usecase seems not to be registered."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			useCaseID = useCase.getId();
		}
		
		RenderReportRequest renderRequest = new RenderReportRequest();
		renderRequest.setReportRegistryItemID(registryItemID);
		renderRequest.setParameters(params);
		PrintReportLayoutUtil.printReportLayoutWithDefaultFormat(renderRequest, useCaseID, monitor);
	}
	
	
	private String addErrMessage(String errorMessages, String addition) {
		if (!"".equals(errorMessages)) //$NON-NLS-1$
			errorMessages = errorMessages + "\n"; //$NON-NLS-1$
		errorMessages = errorMessages + addition;
		return errorMessages;
	}
	

	public void setNextRunParams(Map<String, Object> nextRunParams) {
		this.nextRunParams = nextRunParams;
	}
	
}
