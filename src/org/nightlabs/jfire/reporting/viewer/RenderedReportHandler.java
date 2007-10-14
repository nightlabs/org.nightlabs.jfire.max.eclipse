/**
 * 
 */
package org.nightlabs.jfire.reporting.viewer;


import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.jfire.reporting.layout.PreparedRenderedReportLayout;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;

/**
 * Handles {@link RenderedReportLayout}s that come from the server
 * as their content is specific to their format.
 * 
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public interface RenderedReportHandler {

	
	/**
	 * Takes the given {@link RenderedReportLayout} and prepares
	 * or unpacks it for viewing or other tasks. 
	 * A File referencing the entry of the prepared layout is returned
	 * along with the given report layout.
	 * 
	 * @param monitor Monitor to write progress information to
	 * @param layout The layout to be prepared
	 * @return A {@link PreparedRenderedReportLayout} for the given layout referencing the entry file of the prepared layout.
	 */
	public PreparedRenderedReportLayout prepareRenderedReportLayout(IProgressMonitor monitor, RenderedReportLayout layout);
	
}
