/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.action.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.reporting.ReportingInitialiser;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil.PreparedLayoutL10nData;
import org.nightlabs.jfire.reporting.layout.ReportLayoutLocalisationData;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.util.IOUtil;

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
		newShell.setText("Export layout");
	}

	@Override
	protected Point getPreferredSize() {
		return new Point(400, 400);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		layoutFileName = new LabeledText(wrapper, "Select export file name");
		folderComposite = new FileSelectionComposite(
				wrapper, 
				SWT.NONE, FileSelectionComposite.OPEN_DIR, 
				"Select export folder",
		"Select folder") {
			@Override
			protected void modifyText(ModifyEvent e) {
			}
		};
		return wrapper;
	}

	@Override
	protected void okPressed() {
		ReportLayoutExportInput editorInput = new ReportLayoutExportInput(layoutID);
		String fileName = folderComposite.getFileText();
		String reportID = null;
		if (fileName != null) {
			File exportFile = new File(fileName, layoutFileName.getText());
			reportID = IOUtil.getFileNameWithoutExtension(exportFile.getName());
			try {
				ReportingInitialiser.exportLayoutToTemplateFile(editorInput.getFile(), exportFile);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			PreparedLayoutL10nData l10nData = ReportLayoutL10nUtil.prepareReportLayoutL10nData(editorInput);
			File resourceFolder = new File(fileName, "resource");
			resourceFolder.mkdirs();
			for (ReportLayoutLocalisationData data : l10nData.getLocalisationBundle().values()) {				
				String l10nFileName = reportID;
				if ("".equals(data.getLocale())) //$NON-NLS-1$
					l10nFileName = l10nFileName + ".properties"; //$NON-NLS-1$
				else
					l10nFileName = l10nFileName + "_" + data.getLocale() + ".properties";  //$NON-NLS-1$ //$NON-NLS-2$
				
				try {
					File dataFile = new File(resourceFolder, l10nFileName);
					dataFile.createNewFile();
					InputStream in = data.createLocalisationDataInputStream();
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dataFile));
					try {
						IOUtil.transferStreamData(in, out);
					} finally {
						in.close();
						out.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}		


		}
		super.okPressed();
	}

}
