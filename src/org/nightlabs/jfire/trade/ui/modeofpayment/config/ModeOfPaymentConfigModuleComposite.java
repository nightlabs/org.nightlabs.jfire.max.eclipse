/**
 * 
 */
package org.nightlabs.jfire.trade.ui.modeofpayment.config;

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
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.accounting.pay.config.ModeOfPaymentConfigModule;
import org.nightlabs.jfire.accounting.pay.id.ModeOfPaymentFlavourID;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ui.modeofpayment.ModeOfPaymentFlavourTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Composite that shows the list of {@link ModeOfPaymentFlavour}s in an {@link ModeOfPaymentConfigModule}.
 * It also allows for the addition and removing of entries.
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class ModeOfPaymentConfigModuleComposite extends XComposite {

	/**
	 * Used internally when adding entries.
	 */
	private class AddDialog extends ResizableTitleAreaDialog {
		
		private ModeOfPaymentFlavourTable table;
		private Collection<ModeOfPaymentFlavourID> selectedIDs;
		private Collection<ModeOfPaymentFlavourID> newIDs;
		
		public AddDialog(Shell shell, final Collection<ModeOfPaymentFlavourID> selectedIDs) {
			super(shell, null);
			this.selectedIDs = selectedIDs;
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			table = new ModeOfPaymentFlavourTable(parent, SWT.NONE, AbstractTableComposite.DEFAULT_STYLE_MULTI_BORDER);
			Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.modeofpayment.config.ModeOfPaymentConfigModuleComposite.0")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					AccountingManager am = JFireEjbFactory.getBean(AccountingManager.class, Login.getLogin().getInitialContextProperties());
					final Set<ModeOfPaymentFlavourID> allIDs = am.getAllModeOfPaymentFlavourIDs();
					allIDs.removeAll(selectedIDs);
					table.getDisplay().asyncExec(new Runnable() {
						public void run() {
							table.setModeOfPaymentFlavourIDs(allIDs, new NullProgressMonitor());
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
			setTitle(Messages.getString("org.nightlabs.jfire.trade.ui.modeofpayment.config.ModeOfPaymentConfigModuleComposite.title")); //$NON-NLS-1$
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
			newShell.setText(Messages.getString("org.nightlabs.jfire.trade.ui.modeofpayment.config.ModeOfPaymentConfigModuleComposite.window.title")); //$NON-NLS-1$
		}
		
		public Collection<ModeOfPaymentFlavourID> getNewIDs() {
			return newIDs;
		}
		
	}
	
	private ModeOfPaymentFlavourTable modeOfPaymentFlavourTable;
	private IDirtyStateManager dirtyStateManager;
	
	/**
	 * Construct a new {@link ModeOfPaymentConfigModuleComposite}.
	 * 
	 * @param parent The parent {@link Composite} to use.
	 * @param style The style to apply to the composite;
	 * @param dirtyStateManager The manager to report changes to.
	 */
	public ModeOfPaymentConfigModuleComposite(Composite parent, int style, IDirtyStateManager dirtyStateManager) {
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		
		modeOfPaymentFlavourTable = new ModeOfPaymentFlavourTable(this, SWT.NONE, AbstractTableComposite.DEFAULT_STYLE_MULTI_BORDER);
		
		XComposite buttonWrapper = new XComposite(this, SWT.NONE);
		buttonWrapper.getGridData().grabExcessHorizontalSpace = false;
		
		Button addButton = new Button(buttonWrapper, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.modeofpayment.config.ModeOfPaymentConfigModuleComposite.button.add.text")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<ModeOfPaymentFlavour> flavours = modeOfPaymentFlavourTable.getElements();
				List<ModeOfPaymentFlavourID> flavourIDs = NLJDOHelper.getObjectIDList(flavours);
				if (flavourIDs.size() <= 0)
					return;
				AddDialog dlg = new AddDialog(getShell(), flavourIDs);
				if (dlg.open() == Window.OK && dlg.getNewIDs() != null) {
					flavourIDs.addAll(dlg.getNewIDs());
					modeOfPaymentFlavourTable.setModeOfPaymentFlavourIDs(flavourIDs, new NullProgressMonitor());
					if (ModeOfPaymentConfigModuleComposite.this.dirtyStateManager != null)
						ModeOfPaymentConfigModuleComposite.this.dirtyStateManager.markDirty();
				}
			}
		});
		
		Button removeButton = new Button(buttonWrapper, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.modeofpayment.config.ModeOfPaymentConfigModuleComposite.button.remove.text")); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<ModeOfPaymentFlavour> selection = modeOfPaymentFlavourTable.getSelectedElements();
				if (selection.size() > 0) {
					Collection<ModeOfPaymentFlavour> flavours = modeOfPaymentFlavourTable.getElements();
					List<ModeOfPaymentFlavourID> flavourIDs = NLJDOHelper.getObjectIDList(flavours);
					flavourIDs.removeAll(NLJDOHelper.getObjectIDSet(selection));
					modeOfPaymentFlavourTable.setModeOfPaymentFlavourIDs(flavourIDs, new NullProgressMonitor());
					if (ModeOfPaymentConfigModuleComposite.this.dirtyStateManager != null)
						ModeOfPaymentConfigModuleComposite.this.dirtyStateManager.markDirty();
				}
			}
		});
	}
	
	/**
	 * Update the given {@link ModeOfPaymentConfigModule} to reflect what is currently shown to the user.
	 * @param configModule The config module to udpate.
	 */
	public void updateConfigModule(ModeOfPaymentConfigModule configModule) {
		Set<ModeOfPaymentFlavourID> modeOfPaymentFlavourIDs = NLJDOHelper.getObjectIDSet(modeOfPaymentFlavourTable.getElements());
		configModule.setModeOfPaymentFlavourIDs(modeOfPaymentFlavourIDs);
	}

	/**
	 * Update this composite to show the entries of the given {@link ModeOfPaymentConfigModule}.
	 * @param configModule The config module to represent. 
	 */
	protected void updateComposite(final ModeOfPaymentConfigModule configModule) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.modeofpayment.config.ModeOfPaymentConfigModuleComposite.job.loadModesOfDelivery")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				if (!modeOfPaymentFlavourTable.isDisposed())
					modeOfPaymentFlavourTable.setModeOfPaymentFlavourIDs(configModule.getModeOfPaymentFlavourIDs(), new NullProgressMonitor());
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}
	
}
