/**
 *
 */
package org.nightlabs.jfire.trade.ui.modeofdelivery.config;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.store.StoreManagerRemote;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.store.deliver.config.ModeOfDeliveryConfigModule;
import org.nightlabs.jfire.store.deliver.id.ModeOfDeliveryFlavourID;
import org.nightlabs.jfire.trade.ui.modeofdelivery.ModeOfDeliveryFlavourTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Composite that shows the list of {@link ModeOfDeliveryFlavour}s in an {@link ModeOfDeliveryConfigModule}.
 * It also allows for the addition and removing of entries.
 *
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class ModeOfDeliveryConfigModuleComposite extends XComposite {

	/**
	 * Used internally when adding entries.
	 */
	private class AddDialog extends ResizableTitleAreaDialog {

		private ModeOfDeliveryFlavourTable table;
		private Collection<ModeOfDeliveryFlavourID> selectedIDs;
		private Collection<ModeOfDeliveryFlavourID> newIDs;

		public AddDialog(Shell shell, final Collection<ModeOfDeliveryFlavourID> selectedIDs) {
			super(shell, null);
			this.selectedIDs = selectedIDs;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			table = new ModeOfDeliveryFlavourTable(parent, SWT.NONE, AbstractTableComposite.DEFAULT_STYLE_MULTI_BORDER);
			Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.modeofdelivery.config.ModeOfDeliveryConfigModuleComposite.0")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					StoreManagerRemote am = JFireEjb3Factory.getRemoteBean(StoreManagerRemote.class, Login.getLogin().getInitialContextProperties());
					final Set<ModeOfDeliveryFlavourID> allIDs = am.getAllModeOfDeliveryFlavourIDs();
					allIDs.removeAll(selectedIDs);
					table.getDisplay().asyncExec(new Runnable() {
						public void run() {
							table.setModeOfDeliveryFlavourIDs(allIDs, new NullProgressMonitor());
							table.addSelectionChangedListener(new ISelectionChangedListener() {
								@Override
								public void selectionChanged(SelectionChangedEvent event) {
									newIDs = NLJDOHelper.getObjectIDSet(table.getSelectedElements());
								}

							});
						}
					});
					return Status.OK_STATUS;
				}
			};
			loadJob.schedule();
			setTitle(Messages.getString("org.nightlabs.jfire.trade.ui.modeofdelivery.config.ModeOfDeliveryConfigModuleComposite.title")); //$NON-NLS-1$
			table.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					if (table.getFirstSelectedElement() != null)
						okPressed();
				}
			});
			return table;
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(Messages.getString("org.nightlabs.jfire.trade.ui.modeofdelivery.config.ModeOfDeliveryConfigModuleComposite.window.title")); //$NON-NLS-1$
		}

		public Collection<ModeOfDeliveryFlavourID> getNewIDs() {
			return newIDs;
		}

	}

	private ModeOfDeliveryFlavourTable modeOfDeliveryFlavourTable;
	private IDirtyStateManager dirtyStateManager;

	/**
	 * Construct a new {@link ModeOfDeliveryConfigModuleComposite}.
	 *
	 * @param parent The parent {@link Composite} to use.
	 * @param style The style to apply to the composite;
	 * @param dirtyStateManager The manager to report changes to.
	 */
	public ModeOfDeliveryConfigModuleComposite(Composite parent, int style, IDirtyStateManager dirtyStateManager) {
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;

		modeOfDeliveryFlavourTable = new ModeOfDeliveryFlavourTable(this, SWT.NONE, AbstractTableComposite.DEFAULT_STYLE_MULTI_BORDER);

		XComposite buttonWrapper = new XComposite(this, SWT.NONE);
		buttonWrapper.getGridData().grabExcessHorizontalSpace = false;

		Button addButton = new Button(buttonWrapper, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.modeofdelivery.config.ModeOfDeliveryConfigModuleComposite.button.add.text")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<ModeOfDeliveryFlavour> flavours = modeOfDeliveryFlavourTable.getElements();
				List<ModeOfDeliveryFlavourID> flavourIDs = NLJDOHelper.getObjectIDList(flavours);
// This code will not allow to add any elements to the table if table is completely empty, so I've commented it out. Denis.				
//				if (flavourIDs.size() <= 0)
//					return;
				AddDialog dlg = new AddDialog(getShell(), flavourIDs);
				if (dlg.open() == Window.OK && dlg.getNewIDs() != null) {
					flavourIDs.addAll(dlg.getNewIDs());
					modeOfDeliveryFlavourTable.setModeOfDeliveryFlavourIDs(flavourIDs, new NullProgressMonitor());
					if (ModeOfDeliveryConfigModuleComposite.this.dirtyStateManager != null)
						ModeOfDeliveryConfigModuleComposite.this.dirtyStateManager.markDirty();
				}
			}
		});

		Button removeButton = new Button(buttonWrapper, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.modeofdelivery.config.ModeOfDeliveryConfigModuleComposite.button.remove.text")); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<ModeOfDeliveryFlavour> selection = modeOfDeliveryFlavourTable.getSelectedElements();
				if (selection.size() > 0) {
					Collection<ModeOfDeliveryFlavour> flavours = modeOfDeliveryFlavourTable.getElements();
					List<ModeOfDeliveryFlavourID> flavourIDs = NLJDOHelper.getObjectIDList(flavours);
					flavourIDs.removeAll(NLJDOHelper.getObjectIDSet(selection));
					modeOfDeliveryFlavourTable.setModeOfDeliveryFlavourIDs(flavourIDs, new NullProgressMonitor());
					if (ModeOfDeliveryConfigModuleComposite.this.dirtyStateManager != null)
						ModeOfDeliveryConfigModuleComposite.this.dirtyStateManager.markDirty();
				}
			}
		});
	}

	/**
	 * Update the given {@link ModeOfDeliveryConfigModule} to reflect what is currently shown to the user.
	 * @param configModule The config module to udpate.
	 */
	public void updateConfigModule(ModeOfDeliveryConfigModule configModule) {
		Set<ModeOfDeliveryFlavourID> ModeOfDeliveryFlavourIDs = NLJDOHelper.getObjectIDSet(modeOfDeliveryFlavourTable.getElements());
		configModule.setModeOfDeliveryFlavourIDs(ModeOfDeliveryFlavourIDs);
	}

	/**
	 * Update this composite to show the entries of the given {@link ModeOfDeliveryConfigModule}.
	 * @param configModule The config module to represent.
	 */
	protected void updateComposite(final ModeOfDeliveryConfigModule configModule) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.modeofdelivery.config.ModeOfDeliveryConfigModuleComposite.job.loadModesOfDelivery")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				if (!modeOfDeliveryFlavourTable.isDisposed())
					modeOfDeliveryFlavourTable.setModeOfDeliveryFlavourIDs(configModule.getModeOfDeliveryFlavourIDs(), monitor);
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

}
