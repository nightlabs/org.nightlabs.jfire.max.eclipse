package org.nightlabs.jfire.reporting.ui.viewer.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.rwt.RWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;

public class DefaultReportViewerUtilImpl extends DefaultReportViewerUtil {

	@Override
	protected void internalCreatePDFViewer(Composite parent) {
		
	}

	@Override
	protected String internalGetResourceLocation(
			PreparedRenderedReportLayout preparedLayout) {
		String result = null;
		try {
			InputStream in = new FileInputStream(preparedLayout.getEntryFile());
			RWT.getResourceManager().register(preparedLayout.getEntryFile().getName(), in);
			result = RWT.getResourceManager().getLocation(preparedLayout.getEntryFile().getName());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can't find report file", e);
		}
		return result;
	}

	@Override
	protected void internalUpdatePDFViewer(PreparedRenderedReportLayout layout, StackLayout stack) {
		
	}

}
