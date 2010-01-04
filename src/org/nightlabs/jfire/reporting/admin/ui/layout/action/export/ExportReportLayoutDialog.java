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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.reporting.ReportingInitialiser;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil.PreparedLayoutL10nData;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
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
	
	private Button needZipButton;
	
	private static String ZIP_SUFFIX = ".zip";
	private static String REPORT_LAYOUT_SUFFIX = ".rptdesign";

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
		layoutFileName.setText(layoutID.reportRegistryItemID /*+ ZIP_SUFFIX*/);
		folderComposite = new FileSelectionComposite(
				wrapper,
				SWT.NONE, FileSelectionComposite.OPEN_DIR,
				Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectExportFolder"), //$NON-NLS-1$
		Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectFolder")); //$NON-NLS-1$
		
		needZipButton = new Button(wrapper, SWT.CHECK);
		needZipButton.setText("Export in Zip file");
		needZipButton.setSelection(true);
		
//		needZipButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (needZipButton.getSelection() == true) {
//					layoutFileName.setText(layoutID.reportRegistryItemID + ZIP_SUFFIX);
//				}
//				else {
//					layoutFileName.setText(layoutID.reportRegistryItemID + REPORT_LAYOUT_SUFFIX);
//				}
//			}
//		});
		
		return wrapper;
	}

	@Override
	protected void okPressed() {
		ReportLayoutExportInput editorInput = new ReportLayoutExportInput(layoutID);
		
		String parentName = folderComposite.getFileText();
		if (needZipButton.getSelection() == true) {
			try {
				parentName = IOUtil.createUserTempDir("jfire_report.exported.", null).getPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		String reportID = null;
		if (parentName != null) {
			File exportFile = new File(parentName, layoutFileName.getText() + REPORT_LAYOUT_SUFFIX);
			reportID = IOUtil.getFileNameWithoutExtension(exportFile.getName());
			try {
				ReportingInitialiser.exportLayoutToTemplateFile(editorInput.getFile(), exportFile);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			PreparedLayoutL10nData l10nData = ReportLayoutL10nUtil.prepareReportLayoutL10nData(editorInput);
			File resourceFolder = new File(parentName, "resource"); //$NON-NLS-1$
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

			if (needZipButton.getSelection() == true) {
				File outputFilePath = new File(folderComposite.getFile(), layoutFileName.getText() + ZIP_SUFFIX);
				try {
					IOUtil.zipFolder(outputFilePath, IOUtil.getUserTempDir("jfire_report.exported.", null));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		super.okPressed();
	}

}
