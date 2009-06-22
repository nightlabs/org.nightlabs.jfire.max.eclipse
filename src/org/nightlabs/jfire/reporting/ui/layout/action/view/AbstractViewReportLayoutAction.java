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

package org.nightlabs.jfire.reporting.ui.layout.action.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCase;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCaseRegistry;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule.UseCaseConfig;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard.WizardResult;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewer;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerFactory;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerRegistry;

/**
 * Abstract Action that can be used as basis for actions that show reports in a {@link ReportViewer}.
 * The parameter acquisition and printing is implemented completely, subclasses
 * may override the {@link ReportUseCase} and {@link Locale} used for the printing.
 * 
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 */
public abstract class AbstractViewReportLayoutAction extends ReportRegistryItemAction {

	/**
	 * Create a new {@link AbstractViewReportLayoutAction}.
	 */
	public AbstractViewReportLayoutAction() {
		super();
	}

	/**
	 * Create a new {@link AbstractViewReportLayoutAction}.
	 * 
	 * @param text The actions text.
	 */
	public AbstractViewReportLayoutAction(String text) {
		super(text);
	}

	/**
	 * Create a new {@link AbstractViewReportLayoutAction}.
	 * 
	 * @param text The actions text.
	 * @param image The actions image.
	 */
	public AbstractViewReportLayoutAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * Create a new {@link AbstractViewReportLayoutAction}. 
	 * 
	 * @param text The actions text.
	 * @param style The actions style.
	 */
	public AbstractViewReportLayoutAction(String text, int style) {
		super(text, style);
	}

	/**
	 * Extracts all {@link ReportRegistryItemID} of the given {@link ReportRegistryItem}s
	 * given that they are a {@link ReportLayout} and no {@link ReportCategory}.
	 * 
	 * @param reportRegistryItems The {@link ReportRegistryItem}s to filter.
	 * @return A collection with only {@link ReportRegistryItem}s of {@link ReportLayout}s.
	 */
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
	
	/**
	 * This method can be overridden in order to define a special {@link ReportUseCase}
	 * id that should be used to print the given report. The default implementation
	 * returns <code>null</code> to indicate that the {@link ReportUseCase} should be 
	 * looked up in the configuration or queried from the user.
	 * @param reportID TODO
	 * @param params TODO
	 * @param reportID The id of the report to print.
	 * @param params The parameter the report should be printed with.
	 *  
	 * @return The id of the {@link ReportUseCase} to use, or <code>null</code> to indicate that 
	 * 		the {@link ReportUseCase} appropriate for the given report.
	 */
	protected String getReportUseCaseID(ReportRegistryItemID reportID, Map<String, Object> params) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction#run(java.util.Collection)
	 */
	@Override
	public void run(Collection<ReportRegistryItem> reportRegistryItems) {
		runWithRegistryItemIDs(extractReportLayouts(reportRegistryItems));
	}
	
	private Map<String, Object> nextRunParams = null;
	
	public void runWithRegistryItemIDs(final Collection<ReportRegistryItemID> reportRegistryItems) {
		Job viewJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.action.view.AbstractViewReportLayoutAction.printJob.name")) { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
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
					String useCaseID = getReportUseCaseID(itemID, params);
					if (useCaseID == null) {
						// Try to lookup the UseCase by the reportLayoutType
						ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCaseByLayoutType(itemID.reportRegistryItemType);
						if (useCase != null)
							useCaseID = useCase.getId();
					}
					if (useCaseID == null) {
						// if no usecase found till now, try to find the default/fallback one
						ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCase(ReportingPlugin.DEFAULT_REPORT_USE_CASE_ID);
						if (useCase == null) {
							errorMessages = addErrMessage(errorMessages, "No useCaseID was specified and no useCase could be found for the reportLayoutType: "+itemID.reportRegistryItemType); //$NON-NLS-1$
							continue;
						}
						useCaseID = useCase.getId();
					}
					
					
					ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
					UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(useCaseID);
					String format = "html"; //$NON-NLS-1$
					if (useCaseConfig != null)
						format = useCaseConfig.getViewerFormat();
					Birt.OutputFormat outFormat = Birt.OutputFormat.valueOf(format);
					
					String reportViewerID = null;
					if (useCaseConfig != null)
						reportViewerID = useCaseConfig.getReportViewerID();
					if (reportViewerID == null)
						reportViewerID = ReportViewerRegistry.DEFAULT_REPORT_VIEWER_ID;
					
					ReportViewerFactory viewerFactory = ReportViewerRegistry.sharedInstance().getReportViewerFactory(reportViewerID);
					if (viewerFactory == null)
						viewerFactory = ReportViewerRegistry.sharedInstance().getReportViewerFactory(ReportViewerRegistry.DEFAULT_REPORT_VIEWER_ID);
					
					if (viewerFactory == null) {
						addErrMessage(errorMessages, "No ReportViewerFactory could be found for reportViewerID: '"+reportViewerID+"'"); //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					}

					ReportViewer viewer = viewerFactory.createReportViewer();
					RenderReportRequest renderRequest = new RenderReportRequest(itemID, params, outFormat);
					Locale requestLocale = getRenderRequestLocale(itemID, params);
					if (requestLocale != null) {
						renderRequest.setLocale(requestLocale);
					} else {
						renderRequest.setLocale(Locale.getDefault());
					}
					viewer.showReport(renderRequest);
				}
				if (!"".equals(errorMessages)) //$NON-NLS-1$
					// TODO: Maybe throw typed exception wrapped in RuntimeException
					throw new IllegalStateException(errorMessages);
				return Status.OK_STATUS;
			}
			
		};
		viewJob.schedule();
	}
	
	private String addErrMessage(String errorMessages, String addition) {
		if (!"".equals(errorMessages)) //$NON-NLS-1$
			errorMessages = errorMessages + "\n"; //$NON-NLS-1$
		errorMessages = errorMessages + addition;
		return errorMessages;
	}
	
	/**
	 * This method is consulted to get the locale the report should be rendered for. 
	 * It's default implementation returns the vm's default locale. 
	 * Subclasses may override this method and return an individual locale for each report.
	 * 
	 * @param reportID The id of the report to render.
	 * @param params The parameters of the report.
	 * @return The locale the given report should be rendered for, or <code>null</code> to indicate 
	 * 		that the default locale should be used.
	 */
	protected Locale getRenderRequestLocale(ReportRegistryItemID reportID, Map<String, Object> params) {
		return Locale.getDefault();
	}
	
	/**
	 * Sets the parameters that should be used by this action in its next run.
	 * After the next run the reference to this parameters will be reseted so
	 * that on the next call the {@link ReportParameterWizard} will be shown
	 * if no other parameters were set. 
	 *  
	 * @param nextRunParams The parameters to use for the next run of this action. 
	 */
	public void setNextRunParams(Map<String, Object> nextRunParams) {
		this.nextRunParams = nextRunParams;
	}
}