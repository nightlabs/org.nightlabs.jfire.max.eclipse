/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nightlabs.base.ui.app.AbstractApplication;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.IOUtil;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public abstract class AbstractRenderedReportHandler implements RenderedReportHandler {

	public File createRenderedReportFolder(RenderedReportLayout layout) {
		File file = new File(
				AbstractApplication.getRootDir()+File.separator+
				"report_tmp"+File.separator+ //$NON-NLS-1$
				"rendered"+File.separator+ //$NON-NLS-1$
				layout.getHeader().getReportRegistryItemID().organisationID+"_"+layout.getHeader().getReportRegistryItemID().reportRegistryItemID+File.separator+ //$NON-NLS-1$
				layout.getHeader().getOutputFormat().toString()+File.separator+
				Long.toHexString(layout.getHeader().getTimestamp().getTime())
			);
		ReportingPlugin.createReportTempFolder();
//		if (file.exists()) {
//			if (!Utils.deleteDirectoryRecursively(file))
//				throw new IllegalStateException("Could not delete rendered report tmp folder "+file);
//		}
		if (file.exists())
			return file;
		if (!file.exists()) {
			if (!file.mkdirs())
				throw new IllegalStateException("Could not create rendered report tmp folder "+file); //$NON-NLS-1$
		}
		return file;
	}
	
	/**
	 * Default implementation returns the URL to the layouts entry file (see {@link RenderedReportLayout#getHeader()}).
	 * It will unzip the data first if the header indicates that the entries are zipped.
	 * 
	 * 
	 * @see org.nightlabs.jfire.reporting.ui.viewer.RenderedReportHandler#prepareRenderedReportLayout(ProgressMonitor, RenderedReportLayout)
	 */
	public PreparedRenderedReportLayout prepareRenderedReportLayout(ProgressMonitor monitor, RenderedReportLayout layout) {
		File folder = createRenderedReportFolder(layout);
		File zip = new File(folder, "renderedLayout.zip"); //$NON-NLS-1$
		File file = new File(folder, layout.getHeader().getEntryFileName());
		
		if (!layout.getHeader().isZipped()) {
			// redirect writing to entry file
			zip = file;
		}
		
		try {
			monitor.setTaskName(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.AbstractRenderedReportHandler.saveProgressMonitor.storeLayoutToDiskTask.name")); //$NON-NLS-1$
			if (!zip.exists()) {
//				if (!zip.delete())
//				throw new IllegalStateException("Could not delete zip file "+zip);
//				}

				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
				out.write((byte[])layout.getData());
				out.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (layout.getHeader().isZipped()) {
			monitor.setTaskName(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.AbstractRenderedReportHandler.saveProgressMonitor.unzipLayoutTask.name")); //$NON-NLS-1$
			try {
				IOUtil.unzipArchive(zip, folder);
			} catch (IOException e) {
				throw new IllegalStateException("Could not unzip rendered report layout", e); //$NON-NLS-1$
			}
		}

//		URL fileURL = null;
//		try {
//			fileURL = file.toURL();
//		} catch (MalformedURLException e) {
//			throw new RuntimeException(e);
//		}
		
		return new PreparedRenderedReportLayout(layout, file);
	}
	
}
