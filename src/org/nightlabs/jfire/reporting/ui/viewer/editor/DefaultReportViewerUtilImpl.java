package org.nightlabs.jfire.reporting.ui.viewer.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfrenderer.PDFFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFProgressMontitorWrapper;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.save.SaveAsActionHandler;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PDFViewerComposite;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.IOUtil;

import com.sun.pdfview.PDFFile;

public class DefaultReportViewerUtilImpl extends DefaultReportViewerUtil {

	private PDFViewerComposite pdfViewerComposite;
	private File lastSaveDirectory = null;

	@Override
	public void createPDFViewer(Composite parent) {
		pdfViewerComposite = new PDFViewerComposite(parent, SWT.BORDER);
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

								InputStream in = getPreparedLayout().getEntryFileAsURL().openStream();
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
	public String getResourceLocation() {
		return getPreparedLayout().getEntryFileAsURL().toString();
	}

	@Override
	public void updatePDFViewer(final StackLayout stack) {
		
		org.eclipse.core.runtime.jobs.Job loadJob = new org.eclipse.core.runtime.jobs.Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.loadJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.loadMonitor.task.name"), 100); //$NON-NLS-1$
				try {
					final PDFFile pdfFile = PDFFileLoader.loadPDF(getPreparedLayout().getEntryFileAsURL(), new PDFProgressMontitorWrapper(new SubProgressMonitor(monitor, 20)));
					final PDFDocument pdfDocument = new OneDimensionalPDFDocument(pdfFile, new SubProgressMonitor(monitor, 80));

					pdfViewerComposite.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (pdfViewerComposite.isDisposed())
								return;

							pdfViewerComposite.getPdfViewer().setPDFDocument(pdfDocument);
							stack.topControl = pdfViewerComposite;
							pdfViewerComposite.layout(true, true);
							pdfViewerComposite.getParent().layout(true, true);
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
