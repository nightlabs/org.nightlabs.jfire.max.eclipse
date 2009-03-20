package org.nightlabs.jfire.trade.admin.ui.layout;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ILayout;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public abstract class AbstractLocalLayoutListComposite<L extends ILayout> extends XComposite {
	private static final Logger logger = Logger.getLogger(AbstractLocalLayoutListComposite.class);

	protected LayoutTable<L> layoutTable;

	private Text baseFolderText;
	
	private static final int PREVIEW_MAX_WIDTH = 300;
	private static final int PREVIEW_MAX_HEIGHT = 200;

	/**
	 * 
	 * @param parent
	 * @param style
	 */
	public AbstractLocalLayoutListComposite(Composite parent, int style) {
		super(parent, style);
		
		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.AbstractLocalLayoutListComposite.label.availableLayouts")); //$NON-NLS-1$

		XComposite threeColWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 3);
		label = new Label(threeColWrapper, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.AbstractLocalLayoutListComposite.label.baseFolder")); //$NON-NLS-1$
		
		baseFolderText = new Text(threeColWrapper, getBorderStyle());
		baseFolderText.setEditable(false);
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, baseFolderText);
		Button chooseBaseFolderButton = new Button(threeColWrapper, SWT.PUSH);
		chooseBaseFolderButton.setText("..."); //$NON-NLS-1$
		chooseBaseFolderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(RCPUtil.getActiveShell());
				if (new File(baseFolderText.getText()).isDirectory())
					dialog.setFilterPath(baseFolderText.getText());
				else
					dialog.setFilterPath("."); //$NON-NLS-1$
				
				String directoryName = dialog.open();
				if (directoryName != null) {
					File directoryFile = new File(directoryName);
					if (directoryFile.exists() && directoryFile.isDirectory()) {
						storeInitialBaseFolder(directoryFile);
						baseFolderText.setText(directoryFile.getPath());
						loadLayouts(directoryFile);
					}
				}
			}
		});
		
		XComposite twoColWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA, 2);

		layoutTable = new LayoutTable<L>(twoColWrapper, getBorderStyle());

		String configModuleBaseDirectory = getInitialBaseFolder();
		File baseDirectory = new File(configModuleBaseDirectory);
		if (!baseDirectory.isDirectory())
			baseDirectory = new File("."); //$NON-NLS-1$
			
		baseFolderText.setText(baseDirectory.getAbsolutePath());
		layoutTable.displayLoadingMessage();

		loadLayouts(baseDirectory);
		
		final LayoutPreviewComposite<L> preview = new LayoutPreviewComposite<L>(twoColWrapper, getRenderer(), PREVIEW_MAX_WIDTH, PREVIEW_MAX_HEIGHT);
		layoutTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				preview.showPreview(layoutTable.getFirstSelectedElement());
			}
		});
//		GridData gd = new GridData(GridData.FILL_VERTICAL);
		GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
		gd.widthHint = PREVIEW_MAX_WIDTH;
		gd.heightHint = PREVIEW_MAX_HEIGHT;
		preview.setLayoutData(gd);
	}

	private Job loadLayoutsJob;

	public void loadLayouts(final File baseDirectory) {
		loadLayoutsJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.AbstractLocalLayoutListComposite.job.loadLayouts.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final Job thisJob = this;
				
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (loadLayoutsJob == thisJob)
							layoutTable.displayLoadingMessage();
					}
				});
				
				FilenameFilter ticketLayoutFilenameFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("." + getLayoutFileExtension()); //$NON-NLS-1$
					}
				};

				final Collection<ILayout> layouts = new LinkedList<ILayout>();

				for (File tldFile : baseDirectory.listFiles(ticketLayoutFilenameFilter)) {
					try {
						ILayout layout = createLayoutFromFile(tldFile);
						layout.loadFile(tldFile);
						layouts.add(layout);
					} catch (IOException e) {
						logger.info("Loading the ticket layout file '" + tldFile.getPath() + "' failed.", e); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (loadLayoutsJob == thisJob)
							layoutTable.setInput(layouts);
					}
				});
				
				return Status.OK_STATUS;
			}
		};

		loadLayoutsJob.schedule();
	}
	
	public L getSelectedLayout() {
		return layoutTable.getFirstSelectedElement();
	}
	
	public LayoutTable<L> getLayoutTable() {
		return layoutTable;
	}

	public String getBaseFolder() {
		return baseFolderText.getText();
	}
	
	protected abstract String getInitialBaseFolder();
	protected abstract void storeInitialBaseFolder(File folder);
	protected abstract L createLayoutFromFile(File file) throws IOException;
	protected abstract ILayoutPreviewRenderer<L> getRenderer();
	protected abstract String getLayoutFileExtension();
}
