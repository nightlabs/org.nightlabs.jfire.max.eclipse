package org.nightlabs.jfire.trade.admin.ui.layout;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.ILayout;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class LayoutPreviewComposite<L extends ILayout> extends XComposite {
	
	private static final Logger logger = Logger.getLogger(LayoutPreviewComposite.class);
	
	private Label imageLabel;
	private int maxWidth;
	private int maxHeight;
	private ILayoutPreviewRenderer<L> previewRenderer;

	public LayoutPreviewComposite(Composite parent, ILayoutPreviewRenderer<L> renderer, int maxWidth, int maxHeight) {
		super(parent, SWT.BORDER, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		Label previewLabel = new Label(this, SWT.NONE);
		previewLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutPreviewComposite.group.preview.text")); //$NON-NLS-1$
//		Group group = new Group(this, SWT.NONE);
//		group.setText(Messages.getString("org.nightlabs.crossticket.trade.admin.ui.ticketlayout.editor.LayoutPreviewComposite.group.preview.text")); //$NON-NLS-1$
		
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		
//		imageLabel = new Label(group, SWT.NONE);
		imageLabel = new Label(this, SWT.NONE);
		final GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = maxWidth;
		layoutData.heightHint = maxHeight;
		imageLabel.setLayoutData(layoutData);
		
		previewRenderer = renderer;
	}

	private Map<String, Image> generatedPreviews = Collections.synchronizedMap(new HashMap<String, Image>());
	private Job showPreviewJob;
	
	public void showPreview(final L layout) {
		imageLabel.setImage(null);
		if (layout == null)
			return;
		
		imageLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutPreviewComposite.label.text")); //$NON-NLS-1$
		
		showPreviewJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutPreviewComposite.job.loadPreview.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Image prev = generatedPreviews.get(layout.getFileName());
				
				if (prev == null) {
					try {
						prev = previewRenderer.renderPreview(layout, maxWidth, maxHeight);
						generatedPreviews.put(layout.getFileName(), prev);
					} catch (final Exception e) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								imageLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutPreviewComposite.previewErrorMessage")); //$NON-NLS-1$
								logger.info("Rendering the preview of " + layout.getFileName() + " failed: ", e); //$NON-NLS-1$ //$NON-NLS-2$
							}
						});
						return Status.OK_STATUS;
					}
				}
				
				final Image preview = prev;
				final Job thisJob = this;
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (showPreviewJob == thisJob && !imageLabel.isDisposed()) {
							imageLabel.setText(""); //$NON-NLS-1$
							imageLabel.setImage(preview);
							imageLabel.pack(true);
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		showPreviewJob.setSystem(true);
		showPreviewJob.schedule();
		
		// add a dispose listener to destroy all images when this composite is disposed
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				for (Image image : generatedPreviews.values())
					image.dispose();
			}
		});
	}
}
