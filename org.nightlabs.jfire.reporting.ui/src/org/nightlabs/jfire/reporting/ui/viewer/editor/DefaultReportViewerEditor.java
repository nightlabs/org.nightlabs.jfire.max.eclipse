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

package org.nightlabs.jfire.reporting.ui.viewer.editor;


import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class DefaultReportViewerEditor extends AbstractReportViewerEditor {
	
	public static final String ID_EDITOR = DefaultReportViewerEditor.class.getName();

	private DefaultReportViewerComposite defaultReportViewerComposite;
	
	private RenderReportRequest lastRenderReportRequest = null;
	
	/**
	 * 
	 */
	public DefaultReportViewerEditor() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		defaultReportViewerComposite = new DefaultReportViewerComposite(parent, SWT.NONE);
		defaultReportViewerComposite.setLayoutData(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#showReport(org.nightlabs.jfire.reporting.ui.Birt.OutputFormat)
	 */
	public void showReport(final Birt.OutputFormat format) {
		RenderReportRequest request = getViewerInput().getRenderReportRequest().clone();
		request.setOutputFormat(format);
		getViewerInput().setRenderReportRequest(request);
		defaultReportViewerComposite.showReport(request);
		lastRenderReportRequest = request;
		updateEditorTitle();
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#showReport(org.nightlabs.jfire.reporting.ui.layout.render.RenderedReportLayout)
	 */
	public void showReport(final RenderedReportLayout reportLayout) {
		defaultReportViewerComposite.showReport(reportLayout);
		lastRenderReportRequest = null;
		updateEditorTitle();
	}
	
	private void updateEditorTitle() {
		Job nameJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerEditor.job.loadReportName")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final ReportRegistryItemID reportID = getViewerInput().getRenderReportRequest().getReportRegistryItemID(); 
				final ReportRegistryItem report = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(
						reportID, new String[] {FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_NAME}, monitor);
				defaultReportViewerComposite.getDisplay().asyncExec(new Runnable() {
					public void run() {
						setPartName(report.getName().getText());
					}
				});
				return Status.OK_STATUS;
			}
			
		};
		nameJob.schedule();
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#getReportRegistryItemID()
	 */
	public ReportRegistryItemID getReportRegistryItemID() {
		return getViewerInput().getRenderReportRequest().getReportRegistryItemID();
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#getPreparedRenderedReportLayout()
	 */
	public PreparedRenderedReportLayout getPreparedRenderedReportLayout() {
		return defaultReportViewerComposite.getPreparedLayout();
	}

	@Override
	public RenderReportRequest getLastRenderReportRequest() {
		return lastRenderReportRequest;
	}
}

