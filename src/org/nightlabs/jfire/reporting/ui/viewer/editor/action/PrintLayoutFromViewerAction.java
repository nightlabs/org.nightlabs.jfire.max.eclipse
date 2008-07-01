/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer.editor.action;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

/**
 * Prints the entryURL of a report shown by an {@link ReportViewerEditor}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrintLayoutFromViewerAction extends Action {


	private ReportViewerEditor reportViewerEditor;
	
	/**
	 * @param text
	 */
	public PrintLayoutFromViewerAction(ReportViewerEditor reportViewerEditor) {
		super(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.action.PrintLayoutFromViewerAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(ReportingPlugin.getDefault(), PrintLayoutFromViewerAction.class, "", ImageDimension._24x24)); //$NON-NLS-1$
		this.reportViewerEditor = reportViewerEditor;
	}

	@Override
	public void run() {
		Job printJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.action.PrintLayoutFromViewerAction.printJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
//					PrintReportLayoutUtil.printReportLayout(
//							reportViewerEditor.getReportRegistryItemID(),
//							new File(reportViewerEditor.getReportEntryURL().toURI()),
//							monitor
//						);
					
					if (!PrintReportLayoutUtil.canPrint(reportViewerEditor.getPreparedRenderedReportLayout().getRenderedReportLayout().getHeader().getOutputFormat()))
						return Status.OK_STATUS;
					
					PrintReportLayoutUtil.printReportLayout(
							reportViewerEditor.getReportRegistryItemID(),
							reportViewerEditor.getPreparedRenderedReportLayout().getEntryFile(),
							monitor
						);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		printJob.schedule();
	}
	
	public void setReportViewerEditor(ReportViewerEditor reportViewerEditor) {
		this.reportViewerEditor = reportViewerEditor;
	}
}
