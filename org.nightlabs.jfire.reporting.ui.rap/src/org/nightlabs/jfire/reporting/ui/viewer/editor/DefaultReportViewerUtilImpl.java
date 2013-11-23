package org.nightlabs.jfire.reporting.ui.viewer.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.rwt.RWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

public class DefaultReportViewerUtilImpl extends DefaultReportViewerUtil {

	@Override
	public void createPDFViewer(Composite parent) {
		
	}

	@Override
	public String getResourceLocation() {
		String result = null;
		try {
			InputStream in = new FileInputStream(getPreparedLayout().getEntryFile());
			RWT.getResourceManager().register(getPreparedLayout().getEntryFile().getName(), in);
			result = RWT.getResourceManager().getLocation(getPreparedLayout().getEntryFile().getName());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can't find report file", e);
		}
		return result;
	}

	@Override
	public void updatePDFViewer(StackLayout stack) {
		
	}

}
