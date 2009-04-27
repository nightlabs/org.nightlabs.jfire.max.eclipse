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

package org.nightlabs.jfire.reporting.ui.layout;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.Birt.OutputFormat;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.viewer.RenderedReportHandler;
import org.nightlabs.jfire.reporting.ui.viewer.RenderedReportHandlerRegistry;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

/**
 * Provides access to <code>RenderedReportLayout</code>s
 * that where rendered on the server.
 * <p>
 * Additionally it can server already handled layouts, meaning
 * layouts that were already rendered by the server with
 * certain parameter and already stored on the clients
 * disk. The handled reports will be cached.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class RenderedReportLayoutProvider {

	/**
	 * Container used to store references to already created
	 * and handled report layouts.
	 * <p>
	 * The reference is stored in form of the entry file.
	 *
	 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
	 *
	 */
	public class RenderedReportEntry {
		private Map<OutputFormat, PreparedRenderedReportLayout> format2PreparedLayouts = new HashMap<OutputFormat, PreparedRenderedReportLayout>();

		public PreparedRenderedReportLayout getEntryFile(OutputFormat format) {
			return format2PreparedLayouts.get(format);
		}

		public void setEntryFile(OutputFormat format, PreparedRenderedReportLayout preparedLayout) {
			format2PreparedLayouts.put(format, preparedLayout);
		}
	}


	/**
	 * Key class used to reference handled {@link RenderedReportEntry}s
	 * in the cache. It holds the report layout id and the
	 * params it was generated with.
	 */
	public class RenderedReportKey {
		private ReportRegistryItemID reportRegistryItemID;
		private Map<String, Object> params;

		private RenderedReportKey(ReportRegistryItemID reportRegistryItemID, Map<String, Object> params) {
			if (reportRegistryItemID == null)
				throw new IllegalArgumentException("reportRegistryItemID must not be null!"); //$NON-NLS-1$

			this.reportRegistryItemID = reportRegistryItemID;
			this.params = params;
		}

		@Override
		public int hashCode() {
			return Util.hashCode(reportRegistryItemID) + Util.hashCode(params);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof RenderedReportKey))return false;

			RenderedReportKey other = (RenderedReportKey)obj;
			return
					Util.equals(this.reportRegistryItemID, other.reportRegistryItemID) &&
					Util.equals(this.params, other.params);
		}

		/**
		 * @return the params
		 */
		public Map<String, Object> getParams() {
			return params;
		}

		/**
		 * @return the reportRegistryItemID
		 */
		public ReportRegistryItemID getReportRegistryItemID() {
			return reportRegistryItemID;
		}
	}

	/**
	 *
	 */
	public RenderedReportLayoutProvider() {
		super();
	}

	/**
	 * Returns the entry file for the given {@link RenderedReportLayout}
	 * by using the appropriate {@link RenderedReportHandler}.
	 * <p>
	 * If necessary (not already cached) the server will be asked
	 * to render the report with the given params.
	 *
	 *
	 * @param reportRegistryItemID The report layout id to get the entry for.
	 * @param params The params to render the report with.
	 * @param format The format to render the report to.
	 * @param monitor An {@link IProgressMonitor} to provide feedback.
	 * @return The file that represents the entry for the given rendered report layout ready for use (unpacked and stored on disk).
	 */
	public  PreparedRenderedReportLayout getPreparedRenderedReportLayout(
			RenderReportRequest renderRequest,
//			ReportRegistryItemID reportRegistryItemID,
//			Map<String,Object> params,
//			Birt.OutputFormat format,
			ProgressMonitor monitor
		)
	{
		RenderedReportKey key = new RenderedReportKey(renderRequest.getReportRegistryItemID(), renderRequest.getParameters());
		RenderedReportEntry entry = getRenderedReportEntry(key);
		if (entry == null) {
			entry = new RenderedReportEntry();
		}
		PreparedRenderedReportLayout preparedLayout = entry.getEntryFile(renderRequest.getOutputFormat());
		if (preparedLayout == null) {
			RenderedReportLayout renderedReportLayout = getRenderedReportLayout(renderRequest, monitor);
			RenderedReportHandler handler = RenderedReportHandlerRegistry.sharedInstance().getHandler(renderRequest.getOutputFormat());
			if (handler == null)
				throw new IllegalStateException("No RenderedReportHandler was registered for format "+renderRequest.getOutputFormat()); //$NON-NLS-1$
			preparedLayout = handler.prepareRenderedReportLayout(monitor, renderedReportLayout);
		}
		entry.setEntryFile(renderRequest.getOutputFormat(), preparedLayout);
		return preparedLayout;
	}



	/**
	 * Returns the entry file for the given {@link RenderedReportLayout}
	 * by using the appropriate {@link RenderedReportHandler}.
	 *
	 * @param renderedReportLayout The rendered report to get the entry for.
	 * @param monitor A progress monitor to use.
	 * @return The prepared rendered report layout.
	 */
	public PreparedRenderedReportLayout getPreparedRenderedReportLayout(
			RenderedReportLayout renderedReportLayout,
			ProgressMonitor monitor
		)
	{
		RenderedReportHandler handler = RenderedReportHandlerRegistry.sharedInstance().getHandler(renderedReportLayout.getHeader().getOutputFormat());
		if (handler == null)
			throw new IllegalStateException("No RenderedReportHandler was registered for format "+renderedReportLayout.getHeader().getOutputFormat().toString()); //$NON-NLS-1$
		return handler.prepareRenderedReportLayout(monitor, renderedReportLayout);
	}


	/**
	 * Return the rendered report for the given reportLayoutID and params
	 * created by a {@link ReportManager} on the server.
	 *
	 * @param reportRegistryItemID The reportLayoutID to use.
	 * @param params The params to apply to the layout.
	 * @param format The format to render the layout to.
	 * @param monitor An {@link IProgressMonitor} to provide feedback
	 * @return An {@link RenderedReportLayout} for the given params as it comes from the server.
	 */
	public RenderedReportLayout getRenderedReportLayout(
			RenderReportRequest renderRequest,
//			ReportRegistryItemID reportRegistryItemID,
//			Map<String,Object> params,
//			Birt.OutputFormat format,
			ProgressMonitor monitor
		)
	{
		ReportManagerRemote rm = ReportingPlugin.getReportManager();
		try {
			return rm.renderReportLayout(renderRequest);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get a cached result for the given key.
	 *
	 * @param key The key containing layout id and params.
	 * @return A cached {@link RenderedReportEntry} or <code>null</code>
	 */
	protected RenderedReportEntry getRenderedReportEntry(RenderedReportKey key) {
		return (RenderedReportEntry)Cache.sharedInstance().get(null, key, (String[])null, 0);
	}

	protected void setRenderedReportEntry(RenderedReportKey key, RenderedReportEntry entry) {
		 Cache.sharedInstance().put(null, key, entry, (String[])null, 0);
	}

	private static RenderedReportLayoutProvider sharedInstance;

	public static RenderedReportLayoutProvider sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new RenderedReportLayoutProvider();
		return sharedInstance;
	}

}
