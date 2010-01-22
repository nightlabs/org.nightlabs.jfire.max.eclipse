package org.nightlabs.jfire.reporting.ui.layout;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemDialog.ISelectionVerifier;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

/**
 * Composite that displays the name of a ReportLayout set by
 * {@link #internalSetReportLayoutID(ReportRegistryItemID)} and lets the user choose another one
 * from a dialog.
 * <p>
 * The Composite is capable of displaying a given Text above the widgets it uses to display the
 * selected layout and bring up the selection dialog.
 * </p>
 * <p>
 * The Composite implements {@link ISelectionProvider} and therefore is able to notify
 * {@link ISelectionChangedListener}s of a change in the users choice of ReportLayout.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * 
 */
public class ReportLayoutSelectionComposite extends XComposite implements ISelectionProvider {

	private String caption;
	private Text layoutNameText;
	private ReportRegistryItemID reportLayoutID;
	private ListenerList selectionChangedListeners = new ListenerList();
	private boolean setReportLayoutIDFromOutside = false;

	/**
	 * Creates a new {@link ReportLayoutSelectionComposite} with a default {@link LayoutDataMode} of
	 * {@link LayoutDataMode#GRID_DATA_HORIZONTAL}.
	 * 
	 * @param parent The parent of the new Composite.
	 * @param style The style of the new Composite.
	 * @param caption The caption that should be displayed above the widgets of this Composite. If
	 *            this is <code>null</code> or empty, no caption will be displayed.
	 */
	public ReportLayoutSelectionComposite(Composite parent, int style, String caption) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		this.caption = caption;
		createContents();
	}
	
	/**
	 * Creates a new {@link ReportLayoutSelectionComposite}.
	 * 
	 * @param parent The parent of the new Composite.
	 * @param style The style of the new Composite.
	 * @param layoutDataMode The {@link LayoutDataMode} of the new Composite.
	 * @param caption The caption that should be displayed above the widgets of this Composite. If
	 *            this is <code>null</code> or empty, no caption will be displayed.
	 */
	public ReportLayoutSelectionComposite(Composite parent, int style, LayoutDataMode layoutDataMode, String caption) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER, layoutDataMode);
		this.caption = caption;
		createContents();
	}
	
	/**
	 * Creates the contents of this Composite (Text to show the layout name and the a button to bring up the selection-dialog).
	 */
	protected void createContents() {
		getGridLayout().makeColumnsEqualWidth = false;
		getGridLayout().numColumns = 2;
		
		if (caption != null && !caption.isEmpty()) {
			Label captionLabel = new Label(this, SWT.WRAP);
			captionLabel.setText(caption);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			captionLabel.setLayoutData(gd);
		}
		
		layoutNameText = new Text(this, getBorderStyle());
		layoutNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layoutNameText.setEditable(false);
		
		Button selectLayoutButton = new Button(this, SWT.PUSH);
		selectLayoutButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Collection<ReportRegistryItemID> selectedItems = ReportRegistryItemDialog.openDialog(layoutNameText.getShell(),
						new ISelectionVerifier() {
					
					@Override
					public boolean isSelectionValid(Collection<ReportRegistryItem> reportRegistryItems) {
						if (reportRegistryItems.size() != 1)
							return false;
						ReportRegistryItem item = reportRegistryItems.iterator().next();
						return (item instanceof ReportLayout) && !Util.equals(reportLayoutID, JDOHelper.getObjectId(item));
					}
				});
				
				if (selectedItems != null && selectedItems.size() == 1) {
					internalSetReportLayoutID(selectedItems.iterator().next());
				}
			}
		});
		selectLayoutButton.setText("...");
		selectLayoutButton.setLayoutData(new GridData());
	}

	
	/**
	 * Set the ID of the ReportLayout this Composite displays. The id will be set immediately and a
	 * Job will be started that loads the name of the ReportLayout.
	 * 
	 * @param reportLayoutID The ID of the ReportLayout to display.
	 */
	public void setReportLayoutID(final ReportRegistryItemID reportLayoutID) {
		setReportLayoutIDFromOutside = true;
		try {
			internalSetReportLayoutID(reportLayoutID);
		} finally {
			setReportLayoutIDFromOutside = false;
		}
	}

	/**
	 * Internal method to set the reportLayoutID. Does what
	 * {@link #setReportLayoutID(ReportRegistryItemID)} says.
	 * 
	 * @param reportLayoutID The reportLaoyutID to set.
	 */
	protected void internalSetReportLayoutID(final ReportRegistryItemID reportLayoutID) {
		layoutNameText.getDisplay().syncExec(new Runnable() {
			public void run() {
				layoutNameText.setText("Loading report layout name ...");
			}
		});
		this.reportLayoutID = reportLayoutID;
		Job loadNameJob = new Job("Loading ReportLayout name") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final ReportRegistryItem[] reportLayout = new ReportRegistryItem[1];
				if (reportLayoutID != null) {
					reportLayout[0] = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(reportLayoutID,
						new String[] { FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_NAME }, monitor);
				}
				layoutNameText.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (ReportLayoutSelectionComposite.this.reportLayoutID == reportLayoutID) {
							if (reportLayout[0] != null)
								layoutNameText.setText(reportLayout[0].getName().getText());
							else 
								layoutNameText.setText("No report layout selected");
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadNameJob.schedule();
		if (!setReportLayoutIDFromOutside)
			notifySelectionChangedListeners();
	}

	/**
	 * @return The currently displayed ReportLayout. This was either set by
	 *         {@link #internalSetReportLayoutID(ReportRegistryItemID)} or selected by the user from the
	 *         selection-dialog.
	 */
	public ReportRegistryItemID getReportLayoutID() {
		return reportLayoutID;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * Notifies the {@link ISelectionChangedListener}s.
	 */
	private void notifySelectionChangedListeners() {
		Object[] listeners = selectionChangedListeners.getListeners();
		SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, getSelection());
		for (Object listener : listeners) {
			if (listener instanceof ISelectionChangedListener) {
				((ISelectionChangedListener) listener).selectionChanged(selectionChangedEvent);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @return The selected ReportLayout-ID.
	 */
	@Override
	public ISelection getSelection() {
		if (reportLayoutID != null)
			return new StructuredSelection(reportLayoutID);
		else 
			return new StructuredSelection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof ReportRegistryItemID)
				setReportLayoutID((ReportRegistryItemID) firstElement);
			else
				setReportLayoutID(null);
		} else {
			setReportLayoutID(null);
		}
	}
	
}
