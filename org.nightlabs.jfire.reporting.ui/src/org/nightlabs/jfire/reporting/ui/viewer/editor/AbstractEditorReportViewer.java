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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.Birt.OutputFormat;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewer;

/**
 * An implementation of ReportViewer that simply opens a configurable (by subclassing)
 * RCP editor. It assumes the opened editor to be an instance of {@link ReportViewerEditor}
 * and will ask it to show the report.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class AbstractEditorReportViewer implements ReportViewer {

	/**
	 * 
	 */
	public AbstractEditorReportViewer() {
		super();
	}

	private static Set<OutputFormat> OUTPUT_FORMATS = null;
	
	private static class PartHolder {
		public IWorkbenchPart part;
	}
	
	/**
	 * Overrides the abstract method and by default returns all
	 * available output formats.
	 * 
	 * @see org.nightlabs.jfire.reporting.ui.viewer.ReportViewer#getSupportedOutputFormats()
	 */
	public Set<OutputFormat> getSupportedOutputFormats() {
		if (OUTPUT_FORMATS == null) {
			OUTPUT_FORMATS = new HashSet<OutputFormat>();
			OutputFormat[] formats = Birt.OutputFormat.values();
			for (int i = 0; i < formats.length; i++) {
				OUTPUT_FORMATS.add(formats[i]);
			}
		}
		return OUTPUT_FORMATS;
	}

	protected abstract String getReportViewerEditorID();
	
	/**
	 * {@inheritDoc}
	 * This implementation will open the editor referenced by {@link #getReportViewerEditorID()}
	 * and will call {@link ReportViewerEditor#showReport(OutputFormat)} on the opened
	 * editor.
	 * <p>
	 * The editor will be opened with a {@link ReportViewerEditorInput} that contains
	 * the report layout id and the params the report should be generate with.
	 * 
	 * @see org.nightlabs.jfire.reporting.ui.viewer.ReportViewer#showReport(org.nightlabs.jfire.reporting.ui.layout.id.ReportRegistryItemID, java.util.Map, org.nightlabs.jfire.reporting.ui.Birt.OutputFormat)
	 */
	public void showReport(
//			ReportRegistryItemID reportRegistryItemID,
//			Map<String, Object> params,
//			OutputFormat format
			RenderReportRequest renderRequest
		)
	{
		final ReportViewerEditorInput input = new ReportViewerEditorInput(renderRequest);
		final String editorID = getReportViewerEditorID();
		
		if (editorID == null)
			throw new IllegalStateException("Can not open viewer editor with id '"+editorID+"'"); //$NON-NLS-1$ //$NON-NLS-2$
		
		final PartHolder holder = new PartHolder();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					holder.part = RCPUtil.openEditor(input, editorID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		});
		if (holder.part instanceof ReportViewerEditor) {
			((ReportViewerEditor)holder.part).showReport(renderRequest.getOutputFormat());
		}
	}

	/**
	 * {@inheritDoc}
	 * This implementation will open the editor referenced by {@link #getReportViewerEditorID()}
	 * and will call {@link ReportViewerEditor#showReport(RenderedReportLayout)} on the opened
	 * editor.
	 * <p>
	 * The editor will be opened with a {@link ReportViewerEditorInput} that contains
	 * the report layout id and no parameters as the report was already generated.
	 * 
	 * @see org.nightlabs.jfire.reporting.ui.viewer.ReportViewer#showReport(org.nightlabs.jfire.reporting.ui.layout.id.ReportRegistryItemID, org.nightlabs.jfire.reporting.ui.layout.render.RenderedReportLayout)
	 */
	public void showReport(RenderedReportLayout renderedReportLayout)
	{
		final ReportViewerEditorInput input = new ReportViewerEditorInput(
				new RenderReportRequest(renderedReportLayout.getHeader().getReportRegistryItemID(), null));
		final String editorID = getReportViewerEditorID();
		
		if (editorID == null)
			throw new IllegalStateException("Can not open viewer editor with id '"+editorID+"'"); //$NON-NLS-1$ //$NON-NLS-2$
		
		final PartHolder holder = new PartHolder();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					holder.part = RCPUtil.openEditor(input, editorID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		});
		if (holder.part instanceof ReportViewerEditor) {
			((ReportViewerEditor)holder.part).showReport(renderedReportLayout);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Default implementation returns null.
	 */
	public Object getAdapter(Object adapterObject) {
		return null;
	}
	
}
