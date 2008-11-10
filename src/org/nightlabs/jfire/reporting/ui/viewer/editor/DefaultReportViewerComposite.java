/**
 *
 */
package org.nightlabs.jfire.reporting.ui.viewer.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.save.SaveAsActionHandler;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PdfViewerComposite;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.Birt.OutputFormat;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.layout.render.RenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.config.DefaultReportViewerCfMod;
import org.nightlabs.jfire.reporting.ui.layout.PreparedRenderedReportLayout;
import org.nightlabs.jfire.reporting.ui.layout.RenderedReportLayoutProvider;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.IOUtil;

import com.sun.pdfview.PDFFile;

/**
 * This Composite incorporates two widgets to view
 * reports rendered to pdf and html.
 * <p>
 * It uses the SWT {@link Browser} widget to display the entry
 * URL of an rendered report, this works for html.
 * For pdf it might work as well, if an appropriate pluing
 * is installed for the systems default browser. If
 * not this Composite can also display pdfs via the
 * Adobe Viewer Java bean.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DefaultReportViewerComposite extends XComposite {

	/**
	 * Log4J Logger for {@link DefaultReportViewerComposite}.
	 */
	private static final Logger logger = Logger.getLogger(DefaultReportViewerComposite.class);

	private Composite stack;
	private StackLayout stackLayout;
	private Composite fetchingLayoutComposite;
	// TODO: Maybe add progressmonitor to status composite
	private BrowserWrapperComposite browser;

//	private Composite awtWrapper;
//	private Frame awtFrame;
//	private Viewer viewer;
//	private PdfViewer pdfViewer;
	private PdfViewerComposite pdfViewerComposite;

	/**
	 * The {@link PreparedRenderedReportLayout} for the currently
	 * displayed report.
	 */
	private PreparedRenderedReportLayout preparedLayout;
	/**
	 * Hyperlink displayed when there where errors during report rendering.
	 */
	private ImageHyperlink errorLink;

/*	private static class ThreadDeathWorkaround implements IExceptionHandler {
		private Set<DefaultReportViewerComposite> registeredComposites = new HashSet<DefaultReportViewerComposite>();

		public boolean handleException(ExceptionHandlerParam handlerParam) {
			logger.warn("WORKAROUND for Adobe PDF bean ThreadDeath = log.", handlerParam.getTriggerException()); //$NON-NLS-1$
			return true;
		}

		public void registerComposite(final DefaultReportViewerComposite composite) {
			boolean haveToAddHandler = registeredComposites.size() < 1;
			if (!registeredComposites.contains(composite)) {
				registeredComposites.add(composite);
				composite.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent arg0) {
						unregisterComposite(composite);
					}
				});
			}
			if (haveToAddHandler) {
				ExceptionHandlerRegistry.sharedInstance().addExceptionHandler(ThreadDeath.class.getName(), this);
				logger.info("Added WORKAROUND ExceptionHandler for Adobe PDF bean ThreadDeath"); //$NON-NLS-1$
			}
		}

		private void unregisterComposite(DefaultReportViewerComposite composite) {
			registeredComposites.remove(composite);
			if (registeredComposites.size() < 1) {
				ExceptionHandlerRegistry.sharedInstance().removeExceptionHandler(ThreadDeath.class.getName());
				logger.info("Removed WORKAROUND ExceptionHandler for Adobe PDF bean ThreadDeath"); //$NON-NLS-1$
			}
		}
	}*/

//	private static ThreadDeathWorkaround threadDeathWorkaround = new ThreadDeathWorkaround();

	public DefaultReportViewerComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		stack = new Composite(this, SWT.NONE);
		stack.setLayoutData(new GridData(GridData.FILL_BOTH));
		stackLayout = new StackLayout();
		stack.setLayout(stackLayout);
		fetchingLayoutComposite = new Composite(stack, SWT.NONE);
		fetchingLayoutComposite.setLayout(new GridLayout());
		Label label = new Label(fetchingLayoutComposite, SWT.WRAP);
		label.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.label.text")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browser = new BrowserWrapperComposite(stack, SWT.NONE);

//		pdfViewer = new PdfViewer();
//		pdfViewer.createControl(stack, SWT.BORDER);

		pdfViewerComposite = new PdfViewerComposite(stack, SWT.BORDER);
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
		stackLayout.topControl = fetchingLayoutComposite;
	}

	public void switchToStatus() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				stackLayout.topControl = fetchingLayoutComposite;
				stack.layout(true, true);
			}
		});
	}

	/**
	 * Fetches the {@link RenderedReportLayout} for the given renderRequest
	 * and displays it in the viewer.
	 *
	 * @param renderRequest The render request for the layout to show.
	 */
	public void showReport(final RenderReportRequest renderRequest) {
		switchToStatus();
		Job fetchJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.fetchJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				final PreparedRenderedReportLayout preparedLayout = RenderedReportLayoutProvider.sharedInstance().getPreparedRenderedReportLayout(
						renderRequest, monitor
					);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						updateViewer(renderRequest.getOutputFormat(), preparedLayout);
					}
				});

				return Status.OK_STATUS;
			}

		};
		fetchJob.schedule();
	}

	public void showReport(final RenderedReportLayout reportLayout) {
		switchToStatus();
		Job fetchJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.fetchJob.name")){			 //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				final PreparedRenderedReportLayout preparedLayout = getPreparedRenderedReportLayout(reportLayout, monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						updateViewer(reportLayout.getHeader().getOutputFormat(), preparedLayout);
					}
				});
				return Status.OK_STATUS;
			}
		};
		fetchJob.schedule();
	}

	/**
	 * Uses the {@link RenderedReportLayoutProvider} to prepare (unpack) the given
	 * {@link RenderedReportLayout} and returns the URL to its entry file.
	 *
	 * @param reportLayout The rendered layout to prepare.
	 * @param monitor An {@link ProgressMonitor} to provide feedback.
	 * @return The {@link PreparedRenderedReportLayout} for the the given .
	 */
	protected PreparedRenderedReportLayout getPreparedRenderedReportLayout(RenderedReportLayout reportLayout, ProgressMonitor monitor) {
		return RenderedReportLayoutProvider.sharedInstance().getPreparedRenderedReportLayout(reportLayout, monitor);
	}

	/**
	 * Updates the viewer (switches viewer from browser to adobe bean if necessary)
	 * and displays the given preparedLayout.
	 *
	 * @param format The format the prepared layout is in.
	 * @param preparedLayout The prepared layout.
	 */
	protected void updateViewer(Birt.OutputFormat format, final PreparedRenderedReportLayout preparedLayout) {
		this.preparedLayout = preparedLayout;
		DefaultReportViewerCfMod cfMod = DefaultReportViewerCfMod.sharedInstance();
		if (format == OutputFormat.pdf && !cfMod.isUseInternalBrowserForPDFs()) {
			stackLayout.topControl = pdfViewerComposite;
//			threadDeathWorkaround.registerComposite(this);
			org.eclipse.core.runtime.jobs.Job loadJob = new org.eclipse.core.runtime.jobs.Job(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.loadJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.loadMonitor.task.name"), 100); //$NON-NLS-1$
					try {
						PDFFile pdfFile = PdfFileLoader.loadPdf(preparedLayout.getEntryFileAsURL(), new SubProgressMonitor(monitor, 20));
						pdfViewerComposite.getPdfViewer().setPdfDocument(new OneDimensionalPdfDocument(pdfFile, new SubProgressMonitor(monitor, 80)));
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
		else {
			// Use the browser widget as default for all other
			stackLayout.topControl = browser;
			browser.setUrl(preparedLayout.getEntryFileAsURL().toString());
		}
		if (errorLink != null && !errorLink.isDisposed()) {
			errorLink.dispose();
		}
		if (preparedLayout.getRenderedReportLayout().getHeader().hasRenderingErrors()) {
			errorLink = new ImageHyperlink(this, SWT.NONE);
			errorLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			errorLink.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewerComposite.errorLink.text")); //$NON-NLS-1$
			errorLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(final HyperlinkEvent e) {
					Collection<Throwable> ts = preparedLayout.getRenderedReportLayout().getHeader().getRenderingErrors();
					for (Throwable t : ts) {
						ExceptionHandlerRegistry.asyncHandleException(t);
					}
				}
			});
			errorLink.setImage(
				FieldDecorationRegistry.getDefault().getFieldDecoration(
					FieldDecorationRegistry.DEC_ERROR
				).getImage()
			);
		}
		// WORKAROUND: The marginHeiht = 1 is a workaround for the acrobat viewer, that won't layout well initially otherwise
		stackLayout.marginHeight = 1;
		this.layout(true, true);
	}

	public PreparedRenderedReportLayout getPreparedLayout() {
		return preparedLayout;
	}

	private static File lastSaveDirectory;
}
