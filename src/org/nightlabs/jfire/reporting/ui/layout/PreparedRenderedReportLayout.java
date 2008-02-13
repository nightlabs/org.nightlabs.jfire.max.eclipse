/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;

/**
 * A {@link PreparedRenderedReportLayout} references
 * is created by a {@link RenderedReportHandler} and
 * references a file that represents the entry for the
 * given rendered report layout ready for use (unpacked and stored on disk).
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PreparedRenderedReportLayout {

	private File entryFile;
	private RenderedReportLayout renderedReportLayout;
	
	/**
	 * 
	 */
	public PreparedRenderedReportLayout() {
	}

	/**
	 * Create a new {@link PreparedRenderedReportLayout}.
	 * @param layout The prepared report layout.
	 * @param entryFile The entry file of the report layout.
	 */
	public PreparedRenderedReportLayout(RenderedReportLayout layout, File entryFile) {
		this.renderedReportLayout = layout;
		this.entryFile = entryFile;
	}
	
	/**
	 * @return the entryFile
	 */
	public File getEntryFile() {
		return entryFile;
	}

	/**
	 * @param entryFile the entryFile to set
	 */
	public void setEntryFile(File entryFile) {
		this.entryFile = entryFile;
	}

	/**
	 * @return the renderedReportLayout
	 */
	public RenderedReportLayout getRenderedReportLayout() {
		return renderedReportLayout;
	}

	/**
	 * @param renderedReportLayout the renderedReportLayout to set
	 */
	public void setRenderedReportLayout(RenderedReportLayout renderedReportLayout) {
		this.renderedReportLayout = renderedReportLayout;
	}
	
	/**
	 * Get the {@link #entryFile} of this
	 * prepared layout as {@link URL}.
	 * 
	 * @return The entry file as {@link URL}.
	 */
	public URL getEntryFileAsURL() {
		URL fileURL = null;
		try {
			fileURL = entryFile.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return fileURL;
	}

}
