/**
 *
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.reporting.scheduled.id.ScheduledReportID;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.action.ScheduledReportActionMenuManager;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.editor.ScheduledReportEditor;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.editor.ScheduledReportEditorInput;

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
		final ScheduledReportsTable scheduledReportsTable = new ScheduledReportsTable(parent, SWT.NONE);
		scheduledReportsTable.load();
		scheduledReportsTable.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				ScheduledReportID scheduledReportID = (ScheduledReportID) JDOHelper.getObjectId(scheduledReportsTable
						.getFirstSelectedElement());
				ScheduledReportEditorInput editorInput = new ScheduledReportEditorInput(scheduledReportID);
				try {
					RCPUtil.openEditor(editorInput, ScheduledReportEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		});
		new ScheduledReportActionMenuManager(scheduledReportsTable, this);
	}

}
