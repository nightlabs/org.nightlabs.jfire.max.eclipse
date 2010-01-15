/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ScheduledReportsView extends LSDViewPart {

//	private ScheduledReportsTable scheduledReportsTable;
	
	public ScheduledReportsView() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent) {
		new ScheduledReportsTable(parent, SWT.NONE).load();
	}

}
