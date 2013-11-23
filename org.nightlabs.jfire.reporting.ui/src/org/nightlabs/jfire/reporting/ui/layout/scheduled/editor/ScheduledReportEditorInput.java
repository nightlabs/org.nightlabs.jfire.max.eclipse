/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.reporting.scheduled.id.ScheduledReportID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportEditorInput extends JDOObjectEditorInput<ScheduledReportID> {

	/**
	 * @param jdoObjectID
	 */
	public ScheduledReportEditorInput(ScheduledReportID jdoObjectID) {
		super(jdoObjectID);
	}

	/**
	 * @param jdoObjectID
	 * @param createUniqueInput
	 */
	public ScheduledReportEditorInput(ScheduledReportID jdoObjectID, boolean createUniqueInput) {
		super(jdoObjectID, createUniqueInput);
	}

}
