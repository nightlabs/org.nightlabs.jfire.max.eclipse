/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import org.nightlabs.jfire.reporting.textpart.ReportTextPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartChangedEvent {

	private ReportTextPart reportTextPart;
	
	/**
	 * Create a new {@link ReportTextPartChangedEvent} with the given part.
	 */
	public ReportTextPartChangedEvent(ReportTextPart reportTextPart) {
		this.reportTextPart = reportTextPart;
	}
	
	/**
	 * @return The changed {@link ReportTextPart}.
	 */
	public ReportTextPart getReportTextPart() {
		return reportTextPart;
	}

}
