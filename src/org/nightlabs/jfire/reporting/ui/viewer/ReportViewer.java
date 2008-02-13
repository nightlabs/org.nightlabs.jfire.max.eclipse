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

package org.nightlabs.jfire.reporting.ui.viewer;

import java.util.Set;

import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;

/**
 * Interface to view Reports generated with BIRT within JFire.
 * {@link ReportViewer}s can be obtained by {@link ReportViewerFactory}s
 * that are regitered to the {@link ReportViewerRegistry} by the
 * extension point.
 * <p>
 * A report viewer can be used in two ways.
 * <ul>
 * <li>It can be passed a report layout id, parameters and a desired output format.
 * In this case the report viewer is responsible for triggering
 * the report generation (or taking it from cache). It will than display the report
 * (See {@link #showReport(RenderReportRequest)})
 * </li>
 * <li>The second way can be used when an already generated and rendered report is
 * available an the viewer is only needed to present the report to the user. (See {@link #showReport(ReportRegistryItemID, RenderedReportLayout)})
 * </li>
 * </ul>
 * 
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface ReportViewer {
	
	/**
	 * Returns all BIRT {@link OutputFormat}s supported by this ReportViewer.
	 * @return All BIRT {@link OutputFormat}s supported by this ReportViewer.
	 */
	public Set<Birt.OutputFormat> getSupportedOutputFormats();
	
	/**
	 * Return the adapter for the given adapterObject.
	 * 
	 * @param adapterObject The adapterObject the adapter returned can be based on.
	 * 
	 * @return An Object representing an adapter to the given object, or <code>null</code>.
	 */
	public Object getAdapter(Object adapterObject);
	
	/**
	 * Use this if you want the viewer to generate the report (= trigger its
	 * generation and rendering on the server) and then having it presented to the
	 * user.
	 *
	 * @param renderRequest The request holdint the report layout id to display,
	 * 	the params to generate the report with and
	 * 	the format to render the report to and to display.
	 */
	public void showReport(
			RenderReportRequest renderRequest
		);

	/**
	 * Use this if you already have a rendered report and only want to present it
	 * to the user.
	 * @param renderedReportLayout The actual rendered report. (As generated by a call to the {@link ReportManager}})
	 */
	public void showReport(RenderedReportLayout renderedReportLayout);
}
