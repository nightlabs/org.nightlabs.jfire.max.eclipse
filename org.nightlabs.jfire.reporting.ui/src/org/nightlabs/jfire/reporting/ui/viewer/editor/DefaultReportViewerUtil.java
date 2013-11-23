package org.nightlabs.jfire.reporting.ui.viewer.editor;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;

public abstract class DefaultReportViewerUtil {
	
	private final static String IMPL_NAME = "org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerUtilImpl";
	
	@SuppressWarnings("unchecked")
	public static DefaultReportViewerUtil create() {
		try {
			Class clazz = DefaultReportViewerUtil.class.getClassLoader().loadClass(IMPL_NAME);
			return (DefaultReportViewerUtil) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unable to instantiate: "+IMPL_NAME, e);
		}
	}
	
	private PreparedRenderedReportLayout preparedLayout;
	
	public void setReportLayout(PreparedRenderedReportLayout preparedLayout) {
		this.preparedLayout = preparedLayout;
	}
	
	public PreparedRenderedReportLayout getPreparedLayout() {
		return preparedLayout;
	}
	
	public abstract void createPDFViewer(Composite parent);
	
	public abstract void updatePDFViewer(StackLayout stack);
	
	public abstract String getResourceLocation();

}
