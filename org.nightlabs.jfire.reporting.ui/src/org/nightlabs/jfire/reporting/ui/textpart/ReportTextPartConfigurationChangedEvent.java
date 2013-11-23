/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationChangedEvent {

	private ReportTextPartConfiguration reportTextPartConfiguration;
	private ReportTextPart reportTextPart;
	
	/**
	 * Create a new {@link ReportTextPartConfigurationChangedEvent} with the given part.
	 */
	public ReportTextPartConfigurationChangedEvent(ReportTextPartConfiguration reportTextPartConfiguration, ReportTextPart reportTextPart) {
		this.reportTextPartConfiguration = reportTextPartConfiguration;
		this.reportTextPart = reportTextPart;
	}
	
	/**
	 * @return The changed {@link ReportTextPart}.
	 */
	public ReportTextPart getReportTextPart() {
		return reportTextPart;
	}
	
	/**
	 * @return The changed {@link ReportTextPartConfiguration}.
	 */
	public ReportTextPartConfiguration getReportTextPartConfiguration() {
		return reportTextPartConfiguration;
	}

}
