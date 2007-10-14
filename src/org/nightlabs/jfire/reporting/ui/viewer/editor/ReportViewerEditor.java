package org.nightlabs.jfire.reporting.ui.viewer.editor;


import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.viewer.RenderedReportHandler;

/**
 * Interfaced used by the {@link AbstractEditorReportViewer}
 * to tell opened editors which report to show and in which format.
 * <p>
 * The editor registered to the id that your implementation of 
 * {@link AbstractEditorReportViewer#getReportViewerEditorID()} should 
 * implement this interface.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface ReportViewerEditor {

	/**
	 * Will be called to show the report referenced by the EditorInput
	 * of this Editor in the given format.
	 * <p>
	 * The editor will have a {@link ReportViewerEditorInput} set
	 * with the report layout id to show and the parameters it 
	 * should be generated with.
	 * <p>
	 * This means implementations are responsible for obtaining the
	 * report from the server. They should make use of the helper methods implemented
	 * in {@link AbstractReportViewerEditor}.
	 *  
	 * @param format The format the report referenced by the editor input should be rendered to.
	 */
	public void showReport(Birt.OutputFormat format);
	
	/**
	 * This is called when a ready rendered layout needs to be displayed.
	 * <p>
	 * Implementations should use {@link RenderedReportHandler}s to prepare
	 * the layout for viewing and then display.
	 *  
	 * @param renderedReportLayout The {@link RenderedReportLayout} as it comes from the server
	 */
	public void showReport(RenderedReportLayout renderedReportLayout);
	
	/**
	 * Returns the {@link ReportRegistryItemID} the Editor is 
	 * currently showing.
	 * 
	 * @return The {@link ReportRegistryItemID} the Editor is currently showing.
	 */
	public ReportRegistryItemID getReportRegistryItemID();
	
	/**
	 * Returns the prepared rendered layout the report the Editor is currently showing.
	 * (see {@link PreparedRenderedReportLayout}).
	 * 
	 * @return The prepared rendered layout the Editor is currently showing.
	 */
	public PreparedRenderedReportLayout getPreparedRenderedReportLayout();
}
