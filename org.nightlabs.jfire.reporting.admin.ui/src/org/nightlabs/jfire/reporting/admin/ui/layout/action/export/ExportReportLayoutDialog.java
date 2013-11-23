/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.action.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Dialog to export a layout as needed for the initialisation in the server.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ExportReportLayoutDialog extends ResizableTrayDialog {

	private XComposite wrapper;
	private FileSelectionComposite folderComposite;
	private LabeledText layoutFileName;
	private ReportRegistryItemID layoutID;

	/**
	 * @param parentShell
	 */
	public ExportReportLayoutDialog(Shell parentShell, ReportRegistryItemID layoutID ) {
		super(parentShell, null);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.layoutID = layoutID;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.window.title")); //$NON-NLS-1$
		newShell.setSize(400, 400);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		layoutFileName = new LabeledText(wrapper, Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectExportFileName")); //$NON-NLS-1$
		folderComposite = new FileSelectionComposite(
				wrapper,
				SWT.NONE, FileSelectionComposite.OPEN_DIR,
				Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectExportFolder"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectFolder")); //$NON-NLS-1$

		Job job = new Job("Loading Data...") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				ReportRegistryItemDAO reportRegistryItemDAO = ReportRegistryItemDAO.sharedInstance();
				reportRegistryItem = reportRegistryItemDAO.getReportRegistryItem(
						ExportReportLayoutDialog.this.layoutID, 
						new String[] {FetchPlan.DEFAULT, 
								ReportRegistryItem.FETCH_GROUP_NAME, 
								ReportRegistryItem.FETCH_GROUP_DESCRIPTION, 
								ReportRegistryItem.FETCH_GROUP_PARENT_CATEGORY}, 
								monitor);

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						String layoutFileNameStr = reportRegistryItem.getName().getText().replace("-", "");
						layoutFileNameStr.replace(" ", "");
						layoutFileName.setText(layoutFileNameStr);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.schedule();

		return wrapper;
	}

	private ReportRegistryItem reportRegistryItem;

	/**
	 * Saves a file indicated by fileName to the given {@link InputStream}.
	 */
	public void saveFile(InputStream io, String fileName) throws IOException {
		// Should this method be here?
		File saveFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(saveFile);
		try {
			byte[] buf = new byte[256];
			int read = 0;
			while ((read = io.read(buf)) > 0) {
				fos.write(buf, 0, read);
			}			
		} finally {
			fos.close();
		}
	}
	
	@Override
	protected void okPressed() {
		ReportManagerRemote rmr = JFireEjb3Factory.getRemoteBean(ReportManagerRemote.class, SecurityReflector.getInitialContextProperties());
		InputStream inputStream = new ByteArrayInputStream(rmr.exportReportLayout(layoutFileName.getText(), layoutID));
		if (inputStream != null) {
			try {
				if (folderComposite.getFile() != null) {
					saveFile(inputStream, folderComposite.getFile().getAbsolutePath() + File.separator + layoutFileName.getText() + ".zip");
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				
			}
		} 
		super.okPressed();
	}
}
