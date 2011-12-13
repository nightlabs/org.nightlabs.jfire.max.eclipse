package org.nightlabs.jfire.reporting.ui.viewer.editor;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;

public abstract class DefaultReportViewerUtil {
	
	private static DefaultReportViewerUtil impl;
	
	private final static String IMPL_NAME = "org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerUtilImpl";
	
	public static void createPDFViewer(Composite parent) {
		ensureImpl();
		impl.internalCreatePDFViewer(parent);
	}
	
	public static String getResourceLocation(PreparedRenderedReportLayout layout) {
		ensureImpl();
		return impl.internalGetResourceLocation(layout);
	}
	
	public static void updatePDFViewer(PreparedRenderedReportLayout layout, StackLayout stack) {
		ensureImpl();
		impl.internalUpdatePDFViewer(layout, stack);
	}
	
	private static void ensureImpl() {
		try {
			Class clazz = DefaultReportViewerUtil.class.getClassLoader().loadClass(IMPL_NAME);
			impl = (DefaultReportViewerUtil) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unable to instantiate: "+IMPL_NAME, e);
		}
	}

	protected abstract void internalCreatePDFViewer(Composite parent);
	
	protected abstract void internalUpdatePDFViewer(PreparedRenderedReportLayout layout, StackLayout stack);
	
	protected abstract String internalGetResourceLocation(PreparedRenderedReportLayout layout);

}
