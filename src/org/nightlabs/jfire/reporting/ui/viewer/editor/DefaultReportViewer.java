/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewer;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerFactory;

/**
 * The default report viewer is based an editor base report viewer
 * that supports html (by internal browser widget) and pdf
 * (by either the internal browser or a java pdf viewer by adobe)
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DefaultReportViewer extends AbstractEditorReportViewer {

	public static final String ID_REPORT_VIEWER = DefaultReportViewer.class.getName();
	
	public DefaultReportViewerComposite compositeAdapter;
	
	public static class Factory implements ReportViewerFactory {

		public ReportViewer createReportViewer() {
			return new DefaultReportViewer();
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
			// Nothing to do here
		}
		
		public boolean isAdaptable(Class adapter) {
			return Composite.class.isAssignableFrom(adapter);
		}
	}
	
	/**
	 * 
	 */
	public DefaultReportViewer() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.viewer.editor.AbstractEditorReportViewer#getReportViewerEditorID()
	 */
	@Override
	protected String getReportViewerEditorID() {
		return DefaultReportViewerEditor.ID_EDITOR;
	}

	@Override
	public Object getAdapter(Object adapterObject) {
		if (Composite.class.isInstance(adapterObject)) {
			if (compositeAdapter == null) {
				compositeAdapter = new DefaultReportViewerComposite((Composite) adapterObject, SWT.NONE);
			}
			return compositeAdapter;
		}
		return null;
	}

	
	@Override
	public void showReport(RenderReportRequest renderRequest) {
		if (compositeAdapter == null)
			super.showReport(renderRequest);
		else
			compositeAdapter.showReport(renderRequest);
	}

	
	@Override
	public void showReport(RenderedReportLayout renderedReportLayout)
	{
		if (compositeAdapter == null)
			super.showReport(renderedReportLayout);
		else
			compositeAdapter.showReport(renderedReportLayout);
	}
}
