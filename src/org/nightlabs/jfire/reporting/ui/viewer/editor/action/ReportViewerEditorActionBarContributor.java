/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer.editor.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditor;

/**
 * By now only contributes the print action.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * 
 */
public class ReportViewerEditorActionBarContributor extends
		EditorActionBarContributor {

	private Logger logger = Logger
			.getLogger(ReportViewerEditorActionBarContributor.class);

	/**
	 * 
	 */
	public ReportViewerEditorActionBarContributor() {
	}

	private PrintLayoutFromViewerAction printLayoutFromViewerAction;
	private RefreshLayoutFromViewerAction refreshLayoutFromViewerAction;

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		logger.debug("contributeToToolBar() activeEditor = " + activeEditor); //$NON-NLS-1$
		super.contributeToToolBar(toolBarManager);

		printLayoutFromViewerAction = new PrintLayoutFromViewerAction((ReportViewerEditor) activeEditor);
		refreshLayoutFromViewerAction = new RefreshLayoutFromViewerAction((ReportViewerEditor) activeEditor);
		toolBarManager.add(printLayoutFromViewerAction);
		toolBarManager.add(refreshLayoutFromViewerAction);
	}

	private IEditorPart activeEditor;

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);
		activeEditor = targetEditor;
		if (activeEditor instanceof ReportViewerEditor) {
			printLayoutFromViewerAction
					.setReportViewerEditor((ReportViewerEditor) activeEditor);
			refreshLayoutFromViewerAction
					.setReportViewerEditor((ReportViewerEditor) activeEditor);
		}
	}
}
