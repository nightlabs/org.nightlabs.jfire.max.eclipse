package org.nightlabs.jfire.reporting.ui.viewer.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.rwt.RWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfrenderer.PDFFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFProgressMontitorWrapper;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.save.SaveAsActionHandler;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PDFViewerComposite;

public class DefaultReportViewerUtilImpl extends DefaultReportViewerUtil {

	private PDFViewerComposite pdfViewerComposite;

	@Override
	protected void internalCreatePDFViewer(Composite parent) {
		pdfViewerComposite = new PDFViewerComposite(stack, SWT.BORDER);
		new SaveAsActionHandler(pdfViewerComposite.getPdfViewer()) {
			@Override
			public void saveAs() {
				String suggestedDirectory;
				String suggestedFilePath;
				String suggestedFileName;

				if (lastSaveDirectory == null)
					lastSaveDirectory = IOUtil.getUserHome();

				suggestedFileName = Long.toHexString(System.currentTimeMillis()) + ".pdf"; // TODO is there a way to get a nicer name? i.e. the name of the report and its currently displayed data - e.g. sth. like "Offer-2008-234"??? //$NON-NLS-1$
				File f = new File(lastSaveDirectory, suggestedFileName);
				suggestedDirectory = lastSaveDirectory.getAbsolutePath();
				suggestedFilePath = f.getAbsolutePath();

				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.SAVE);
				fileDialog.setFileName(suggestedFileName);

				if (suggestedFilePath != null && !"".equals(suggestedFilePath)) //$NON-NLS-1$
					fileDialog.setFilterPath(suggestedDirectory);

				fileDialog.setText(String.format("Save PDF file %s", suggestedFileName, suggestedFilePath)); //$NON-NLS-1$
				String fileName = fileDialog.open();
				if (fileName != null) {
					final File file = new File(fileName);
					if (file.exists()) {
						if (!MessageDialog.openQuestion(RCPUtil.getActiveShell(), String.format("Overwrite?", file.getName(), file.getAbsolutePath()), String.format("The file \"%s\" already exists. Do you want to overwrite it?", file.getName(), file.getAbsolutePath()))) //$NON-NLS-1$ //$NON-NLS-2$
							return;
					}

					lastSaveDirectory = file.getParentFile();

					Job job = new Job(String.format("Saving PDF file %s", file.getName())) { //$NON-NLS-1$
						@Override
						protected IStatus run(ProgressMonitor monitor)
						throws Exception
						{
							monitor.beginTask(String.format("Saving PDF file %s", file.getName()), 100); //$NON-NLS-1$
							try {
								monitor.worked(10);

								InputStream in = preparedLayout.getEntryFileAsURL().openStream();
								try {
									FileOutputStream out = new FileOutputStream(file);
									try {
										IOUtil.transferStreamData(in, out);
									} finally {
										out.close();
									}
								} finally {
									in.close();
								}

							} finally {
								monitor.done();
							}
							return Status.OK_STATUS;
						}
					};
					job.schedule();
				}
			}
		};
	}

	@Override
	protected String internalGetResourceLocation(
			PreparedRenderedReportLayout preparedLayout) {
		return preparedLayout.getEntryFileAsURL().toString();
	}

	@Override
	protected void internalUpdatePDFViewer(PreparedRenderedReportLayout layout) {
		org.eclipse.core.runtime.jobs.Job loadJob = new org.eclipse.core.runtime.jobs.Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.loadJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.loadMonitor.task.name"), 100); //$NON-NLS-1$
				try {
					final PDFFile pdfFile = PDFFileLoader.loadPDF(preparedLayout.getEntryFileAsURL(), new PDFProgressMontitorWrapper(new SubProgressMonitor(monitor, 20)));
					final PDFDocument pdfDocument = new OneDimensionalPDFDocument(pdfFile, new SubProgressMonitor(monitor, 80));

					display.asyncExec(new Runnable() {
						public void run() {
							if (isDisposed())
								return;

							pdfViewerComposite.getPdfViewer().setPDFDocument(pdfDocument);
							stackLayout.topControl = pdfViewerComposite;
							DefaultReportViewerComposite.this.layout(true, true);
						}
					});
				}
				catch (final Exception x) {
					throw new RuntimeException(x);
				}
				finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();
	}

}
