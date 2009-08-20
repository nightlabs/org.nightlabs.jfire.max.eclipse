/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer.editor.action;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor;

/**
 * Prints the entryURL of a report shown by an {@link ReportViewerEditor}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class RefreshLayoutFromViewerAction extends Action {


	private ReportViewerEditor reportViewerEditor;
	
	/**
	 * @param text
	 */
	public RefreshLayoutFromViewerAction(ReportViewerEditor reportViewerEditor) {
		super(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.action.RefreshLayoutFromViewerAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(ReportingPlugin.getDefault(), RefreshLayoutFromViewerAction.class, "", ImageDimension._24x24)); //$NON-NLS-1$
		this.reportViewerEditor = reportViewerEditor;
	}

	@Override
	public void run() {
		RenderReportRequest lastRenderReportRequest = reportViewerEditor.getLastRenderReportRequest();
		if (lastRenderReportRequest != null) {
			reportViewerEditor.showReport(lastRenderReportRequest.getOutputFormat());
		}
	}
	
	public void setReportViewerEditor(ReportViewerEditor reportViewerEditor) {
		this.reportViewerEditor = reportViewerEditor;
	}
}
