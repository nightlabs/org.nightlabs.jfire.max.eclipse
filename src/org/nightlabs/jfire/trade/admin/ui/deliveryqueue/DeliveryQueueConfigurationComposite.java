package org.nightlabs.jfire.trade.admin.ui.deliveryqueue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;
import org.nightlabs.jfire.store.deliver.DeliveryQueueDAO;
import org.nightlabs.jfire.store.deliver.id.DeliveryQueueID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

public class DeliveryQueueConfigurationComposite extends XComposite {
	private DeliveryQueueTableComposite pqTableComposite;
	private Button addQueueButton;
	private Button editQueueButton;
	private Button delQueueButton;

	private Collection<DeliveryQueue> deliveryQueues;
	private Collection<DeliveryQueue> changedDeliveryQueues = new LinkedList<DeliveryQueue>();

	private IDirtyStateManager dirtyStateManager;
	private DeliveryQueueConfigModule cfMod;

	private boolean pqsLoaded = false;

	public DeliveryQueueConfigurationComposite(Composite parent, IDirtyStateManager dirtyStateManager) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		this.dirtyStateManager = dirtyStateManager;

		XComposite wrapper = new XComposite(this, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(gd); // Spacer

		XComposite spaceWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		new Label(spaceWrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.visibleDeliveryQueuesLabel.text")); //$NON-NLS-1$
		new Button(wrapper, SWT.NONE).setVisible(false);

		pqTableComposite = new DeliveryQueueTableComposite(spaceWrapper);
		pqTableComposite.addCheckStateChangedListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!pqsLoaded)
					return;

				if (! isReadOnly())
					DeliveryQueueConfigurationComposite.this.dirtyStateManager.markDirty();
			}
		});
		pqTableComposite.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof DeliveryQueue && e2 instanceof DeliveryQueue) {
					return ((DeliveryQueue) e1).getName().getText().compareTo(((DeliveryQueue) e2).getName().getText());
				}

				return 0;
			}
		});
		spaceWrapper.getGridData().verticalSpan = 5;
		XComposite.configureLayout(LayoutMode.ORDINARY_WRAPPER, pqTableComposite.getGridLayout());

		Composite buttonComp = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		addQueueButton = new Button(buttonComp, SWT.NONE);
		addQueueButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addQueueButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.addQueueButton.text")); //$NON-NLS-1$
		addQueueButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				addDeliveryQueue();
			}
		});

		editQueueButton = new Button(buttonComp, SWT.NONE);
		editQueueButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editQueueButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.editQueueButton.text")); //$NON-NLS-1$
		editQueueButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				editSelectedDeliveryQueue();
			}
		});

		delQueueButton = new Button(buttonComp, SWT.NONE);
		delQueueButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		delQueueButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.removeQueueButton.text")); //$NON-NLS-1$
		delQueueButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				deleteSelectedDeliveryQueue();
			}
		});

		populateDeliveryQueueList();
	}

	void populateDeliveryQueueList() {
		String loadingMsg = Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.loadDeliveryQueuesJob.name"); //$NON-NLS-1$
		pqTableComposite.setInput(new String[] { loadingMsg });

		Job loadJob = new Job(loadingMsg) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					StoreManager storeManager = getStoreManager();
					Collection<DeliveryQueueID> deliveryQueueIds = storeManager.getAvailableDeliveryQueueIDs(false);
					deliveryQueues = DeliveryQueueDAO.sharedInstance().getDeliveryQueues(deliveryQueueIds, new String[] {DeliveryQueue.FETCH_GROUP_NAME, DeliveryQueue.FETCH_GROUP_HAS_PENDING_DELIVERIES}, 1, new NullProgressMonitor());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						pqTableComposite.setInput(deliveryQueues);
						pqsLoaded = true;

						if (cfMod != null) {
							pqTableComposite.setCheckedElements(cfMod.getVisibleDeliveryQueues());
						}
					}
				});

				return Status.OK_STATUS;
			}
		};

		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();
	}

	protected void updateGUI() {
		pqTableComposite.refresh(true);
	}

	/**
	 * Flag whether the displayed delivery queues are read only or not.
	 */
	private boolean readOnly = false;

	/**
	 * Sets whether the delivery queue configuration is read only or not.
	 * @param readOnly  whether the delivery queue configuration is read only or not.
	 */
	public void setReadOnly(boolean readOnly)
	{
		if (this.readOnly == readOnly)
			return;

		this.readOnly = readOnly;
		final boolean editable = ! readOnly;
		addQueueButton.setEnabled(editable);
		editQueueButton.setEnabled(editable);
		delQueueButton.setEnabled(editable);
		pqTableComposite.setEditable(editable);
	}

	/**
	 * Returns whether the delivery queue configuration is read only or not.
	 * @return whether the delivery queue configuration is read only or not.
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}

	private List<DeliveryQueue> checkedQueues;
	private void storeCheckedDeliveryQueues() {
		checkedQueues = pqTableComposite.getCheckedElements();
	}

	private void restoreCheckedDeliveryQueues() {
		if (checkedQueues != null) {
			pqTableComposite.setCheckedElements(checkedQueues);
			checkedQueues = null;
		}
	}

	void addDeliveryQueue() {
		DeliveryQueue deliveryQueue = new DeliveryQueue(SecurityReflector.getUserDescriptor().getOrganisationID());

		Dialog dialog = new DeliveryQueueNameEditDialog(RCPUtil.getActiveShell(), deliveryQueue);

		if (dialog.open() == Window.OK) {
			deliveryQueues.add(deliveryQueue);
			changedDeliveryQueues.add(deliveryQueue);

			// TODO WORKAROUND to circumvent the JFace bug that checkbox states are not correctly updated
			// when an element is deleted or removed from the model and the view is refreshed.
			// We therefore remember the checked queues before updating the view and reset them thereafter.
			storeCheckedDeliveryQueues();
			updateGUI();
			restoreCheckedDeliveryQueues();

			dirtyStateManager.markDirty();
		}
	}

	void editSelectedDeliveryQueue() {
		DeliveryQueue deliveryQueue = pqTableComposite.getFirstSelectedElement();
		Dialog dialog = new DeliveryQueueNameEditDialog(RCPUtil.getActiveShell(), deliveryQueue);
		if (dialog.open() == Window.OK) {
			// TODO WORKAROUND to circumvent the JFace bug that checkbox states are not correctly updated
			// when an element is deleted or removed from the model and the view is refreshed.
			// We therefore delete the element from the model and the view manually without refreshing the view.
			changedDeliveryQueues.add(deliveryQueue);
//			pqTableComposite.getTableViewer().add(deliveryQueue);
			// END WORKAROUND

			storeCheckedDeliveryQueues();
			updateGUI();
			restoreCheckedDeliveryQueues();

			dirtyStateManager.markDirty();
		}
	}

	void deleteSelectedDeliveryQueue() {
		if (MessageDialog.openQuestion(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.deleteConfirmationDialog.title"), Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.deleteConfirmationDialog.message")) == true) { //$NON-NLS-1$ //$NON-NLS-2$
			DeliveryQueue selectedQueue = pqTableComposite.getFirstSelectedElement();

			// Only delivery queues that have yet been persisted have to be marked as deleted.
			// All others can simply be deleted
			if (JDOHelper.isDetached(selectedQueue)) {
				if (selectedQueue.hasPendingDeliveries()) {
					String message = Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.DeliveryQueueDeletionImpossible.message"); //$NON-NLS-1$
					MessageDialog.openError(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueConfigurationComposite.DeliveryQueueDeletionImpossible.title"), message); //$NON-NLS-1$
					return;
				}
				selectedQueue.markDefunct();
				changedDeliveryQueues.add(selectedQueue);
			}

			deliveryQueues.remove(selectedQueue);

			// TODO WORKAROUND to circumvent the JFace bug that checkbox states are not correctly updated
			// when an element is deleted or removed from the model and the view is refreshed.
			// We therefore delete the element from the model and the view manually without refreshing the view.
			pqTableComposite.removeElement(selectedQueue);
			// END WORKAROUND

			updateGUI();
			dirtyStateManager.markDirty();
		}
	}

	void storeChanges(DeliveryQueueConfigModule configModule) {
		try {
			StoreManager storeManager = getStoreManager();

			// cleanup the list of delivery queues that have to be persisted manually
			List<DeliveryQueue> visibleDeliveryQueues = pqTableComposite.getCheckedElements();
			for (DeliveryQueue pq : visibleDeliveryQueues) {
				changedDeliveryQueues.remove(pq);
			}
			// end cleanup

			storeManager.storeDeliveryQueues(changedDeliveryQueues, false, null, 1);

			configModule.setVisibleDeliveryQueues(visibleDeliveryQueues);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private StoreManager storeManager;

	private StoreManager getStoreManager() {
		if (storeManager != null)
			return storeManager;

		try {
			storeManager = JFireEjbUtil.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
			return storeManager;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void loadData(DeliveryQueueConfigModule configModule) {
		this.cfMod = configModule;
		if (cfMod != null) {
			pqTableComposite.setCheckedElements(cfMod.getVisibleDeliveryQueues());
		}
		updateGUI();
	}
}