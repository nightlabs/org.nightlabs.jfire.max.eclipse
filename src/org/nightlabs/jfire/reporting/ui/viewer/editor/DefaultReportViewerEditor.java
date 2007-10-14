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


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class DefaultReportViewerEditor extends AbstractReportViewerEditor {
	
	public static final String ID_EDITOR = DefaultReportViewerEditor.class.getName();

	private DefaultReportViewerComposite defaultReportViewerComposite;
	
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
		RenderReportRequest request = new RenderReportRequest(
				getViewerInput().getReportRegistryItemID(),
				getViewerInput().getReportParams(),
				format
		);
		defaultReportViewerComposite.showReport(request);
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#showReport(org.nightlabs.jfire.reporting.ui.layout.render.RenderedReportLayout)
	 */
	public void showReport(final RenderedReportLayout reportLayout) {
		defaultReportViewerComposite.showReport(reportLayout);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#getReportRegistryItemID()
	 */
	public ReportRegistryItemID getReportRegistryItemID() {
		return getViewerInput().getReportRegistryItemID();
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor#getPreparedRenderedReportLayout()
	 */
	public PreparedRenderedReportLayout getPreparedRenderedReportLayout() {
		return defaultReportViewerComposite.getPreparedLayout();
	}
}

