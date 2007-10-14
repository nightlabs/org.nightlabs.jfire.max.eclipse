/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer;

import org.eclipse.core.runtime.IExecutableExtension;

/**
 * Factory used by {@link ReportViewerRegistry} in order to create new
 * {@link ReportViewer}s. This is actually registered by the extension point
 * "reportViewer". 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface ReportViewerFactory extends IExecutableExtension {

	/**
	 * Create a new {@link ReportViewer}.
	 * 
	 * @return A new ReportViewer.
	 */
	public ReportViewer createReportViewer();
	
	/**
	 * Check if adaptable with the given adapter.
	 * 
	 * @return Whether or not {@link ReportViewer}s created by this
	 * factory are adaptable with the given adapter class. 
	 */
	public boolean isAdaptable(Class adapter);
}
