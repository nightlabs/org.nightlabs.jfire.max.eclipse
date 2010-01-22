/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jfire.base.ui.timer.TaskDetailSection;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.timer.Task;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportTaskSection extends TaskDetailSection {

	public ScheduledReportTaskSection(IFormPage page, Composite parent) {
		super(page, parent, "Task scheduling", "Enable render of scheduled report");
	}

	@Override
	protected Task getTask(Object input) {
		if (input instanceof ScheduledReport) {
			return ((ScheduledReport) input).getTask();
		}
		return null;
	}
}
