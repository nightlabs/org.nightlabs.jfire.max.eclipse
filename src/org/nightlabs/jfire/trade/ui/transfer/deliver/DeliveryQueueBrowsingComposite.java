package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.ComboComposite;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryQueueDeliveryWizard;
import org.nightlabs.progress.NullProgressMonitor;

class DeliveryQueueBrowsingComposite extends FadeableComposite {
	private ComboComposite<DeliveryQueue> printQueueCombo;
	private DeliveryTable deliveryTable;
	private DeliveryQueueConfigModule deliveryQueueConfigModule;
	private boolean refreshing = false;
	
	public DeliveryQueueBrowsingComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		printQueueCombo = new ComboComposite<DeliveryQueue>(this, SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DeliveryQueue) {
					DeliveryQueue printQueue = (DeliveryQueue) element;
					return printQueue.getName().getText();
				}
				return super.getText(element);
			}
		});
		printQueueCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				deliveryTable.setInput(printQueueCombo.getSelectedElement().getPendingDeliveries());
			}
		});
		deliveryTable = new DeliveryTable(this, SWT.NONE);
		
 		refreshDeliveryQueues();
	}
	
	private DeliveryQueueConfigModule getDeliveryQueueConfigModule () {
		String[] fetchGroups = new String[] { DeliveryQueueConfigModule.FETCH_GROUP_VISIBLE_DELIVERY_QUEUES, DeliveryQueue.FETCH_GROUP_NAME,
				DeliveryQueue.FETCH_GROUP_PENDING_DELIVERY_SET, Delivery.FETCH_GROUP_DELIVERY_TABLE_DATA, LegalEntity.FETCH_GROUP_PERSON,
				User.FETCH_GROUP_PERSON, FetchPlan.DEFAULT };
		DeliveryQueueConfigModule printQueueConfigModule =
				(DeliveryQueueConfigModule) ConfigUtil.getUserCfMod(DeliveryQueueConfigModule.class, fetchGroups, -1, new NullProgressMonitor());
		
		return printQueueConfigModule;
	}
	
	void deliverCheckedDeliveries() {
		final List<Delivery> checkedDeliveries = deliveryTable.getCheckedElements();
		if (checkedDeliveries.isEmpty())
			return;

		//		CombiTransferArticlesWizard transferArticlesWizard = new CombiTransferArticlesWizard(articleIDs, AbstractCombiTransferWizard.TRANSFER_MODE_DELIVERY, DeliveryWizard.SIDE_VENDOR);
		DeliveryQueueDeliveryWizard deliverWizard = new DeliveryQueueDeliveryWizard(checkedDeliveries);
		WizardDialog wizardDialog = new WizardDialog(RCPUtil.getActiveShell(), deliverWizard);
		
		final DeliveryQueue selectedDeliveryQueue = printQueueCombo.getSelectedElement();
		wizardDialog.open();
	}
	
	void refreshDeliveryQueues() {
		
		if (refreshing)
			return;
		
		setFaded(true);
		refreshing = true;
		Job refreshJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.refreshDeliveryQueuesJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final DeliveryQueueConfigModule pqCfMod = getDeliveryQueueConfigModule();
				
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						// store selection
						DeliveryQueue selectedDeliveryQueue = printQueueCombo.getSelectedElement();
						
						// reload delivery queues
						List<DeliveryQueue> visibleDeliveryQueues = pqCfMod.getVisibleDeliveryQueues();
						Collections.sort(visibleDeliveryQueues, new Comparator<DeliveryQueue>() {
							public int compare(DeliveryQueue o1, DeliveryQueue o2) {
								return o1.getName().getText().compareTo(o2.getName().getText());
							}
						});
						
						printQueueCombo.setInput(visibleDeliveryQueues);
						
						// restore selection
						if (selectedDeliveryQueue != null) {
							printQueueCombo.setSelection(selectedDeliveryQueue);
							deliveryTable.setInput(printQueueCombo.getSelectedElement().getPendingDeliveries());
						} else if (!printQueueCombo.getElements().isEmpty()){
							printQueueCombo.setSelection(0);
							deliveryTable.setInput(printQueueCombo.getSelectedElement().getPendingDeliveries());
						}
						
						setFaded(false);
						refreshing = false;
					}
				});
				return Status.OK_STATUS;
			}
		};
		
		refreshJob.setPriority(Job.SHORT);
		refreshJob.schedule();
	}
	
	private StoreManager storeManager;
	
	private StoreManager getStoreManager() {
		if (storeManager != null)
			return storeManager;
		
		try {
			storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			return storeManager;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
