/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer;

/**
 * Thrown when the attempt to lookup a {@link ReportViewer} or {@link ReportViewerFactory}
 * fails for some reason. This might occur if no default report layout id is defined, or
 * the desired format is not supported by a certain viewer.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class NoReportViewerFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NoReportViewerFoundException() {
	}

	/**
	 * @param message
	 */
	public NoReportViewerFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoReportViewerFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoReportViewerFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
