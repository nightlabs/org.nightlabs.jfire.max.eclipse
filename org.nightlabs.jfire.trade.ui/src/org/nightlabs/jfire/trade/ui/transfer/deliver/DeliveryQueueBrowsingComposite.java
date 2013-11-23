package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComboComposite.CaptionOrientation;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;
import org.nightlabs.jfire.store.deliver.id.DeliveryQueueID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryQueueDeliveryWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

class DeliveryQueueBrowsingComposite
extends FadeableComposite
{
	private XComboComposite<DeliveryQueue> printQueueCombo;
	private DeliveryTable deliveryTable;
//	private DeliveryQueueConfigModule deliveryQueueConfigModule;
	private boolean refreshing = false;

	private NotificationListener deliveryQueueLifecycleListener = new NotificationAdapterJob() {
    public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
      for (Iterator<DirtyObjectID> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
        DirtyObjectID dirtyObjectID = it.next();

        Set<DeliveryQueueID> objectIds = NLJDOHelper.getObjectIDSet(printQueueCombo.getElements());
				if (!objectIds.contains(dirtyObjectID.getObjectID())) {
        	return;
        }

        switch (dirtyObjectID.getLifecycleState()) {
          case DIRTY:
          	refreshDeliveryQueues();
            break;
        }
      }
    }
  };

	public DeliveryQueueBrowsingComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		printQueueCombo = new XComboComposite<DeliveryQueue>(this, SWT.READ_ONLY, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.combo.label"), new LabelProvider() { //$NON-NLS-1$
			@Override
			public String getText(Object element) {
				if (element instanceof DeliveryQueue) {
					DeliveryQueue printQueue = (DeliveryQueue) element;
					return printQueue.getName().getText();
				}
				return super.getText(element);
			}
		}, CaptionOrientation.LEFT);
		printQueueCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				deliveryTable.setInput(printQueueCombo.getSelectedElement().getPendingDeliveries());
			}
		});
		deliveryTable = new DeliveryTable(this, SWT.NONE);

		MenuManager popupMenu = new MenuManager();
    IAction checkAllAction = new Action() {
    	@Override
    	public void run() {
    		checkAllDeliveries();
    	}
    };
    checkAllAction.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.button.checkAllDeliveries")); //$NON-NLS-1$

    IAction uncheckAllAction = new Action() {
    	@Override
    	public void run() {
    		uncheckAllDeliveries();
    	}
    };
    uncheckAllAction.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.button.uncheckAllDeliveries")); //$NON-NLS-1$

    popupMenu.add(checkAllAction);
    popupMenu.add(uncheckAllAction);
    Menu menu = popupMenu.createContextMenu(deliveryTable.getTableViewer().getTable());
    deliveryTable.getTableViewer().getTable().setMenu(menu);

		JDOLifecycleManager.sharedInstance().addNotificationListener(DeliveryQueue.class, deliveryQueueLifecycleListener);

    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent event)
      {
        JDOLifecycleManager.sharedInstance().removeNotificationListener(DeliveryQueue.class, deliveryQueueLifecycleListener);
      }
    });

 		refreshDeliveryQueues();
	}

	private DeliveryQueueConfigModule getDeliveryQueueConfigModule () {
		String[] fetchGroups = new String[] { DeliveryQueueConfigModule.FETCH_GROUP_VISIBLE_DELIVERY_QUEUES, DeliveryQueue.FETCH_GROUP_NAME,
				DeliveryQueue.FETCH_GROUP_PENDING_DELIVERY_SET, Delivery.FETCH_GROUP_DELIVERY_TABLE_DATA, Article.FETCH_GROUP_ORDER,
				LegalEntity.FETCH_GROUP_PERSON,	User.FETCH_GROUP_PERSON, FetchPlan.DEFAULT };
		DeliveryQueueConfigModule deliveryQueueConfigModule =
				ConfigUtil.getUserCfMod(DeliveryQueueConfigModule.class, fetchGroups, -1, new NullProgressMonitor());

		return deliveryQueueConfigModule;
	}

	void deliverCheckedDeliveries() {
		final List<Delivery> checkedDeliveries = deliveryTable.getCheckedElements();
		if (checkedDeliveries.isEmpty())
			return;

		AnchorID customerID = null;
		for (Delivery delivery : checkedDeliveries) {
			if (customerID != null && !delivery.getPartnerID().equals(customerID)) {
				MessageDialog.openError(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.dialog.title"), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.dialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			customerID = delivery.getPartnerID();
		}

		final DeliveryQueue selectedDeliveryQueue = printQueueCombo.getSelectedElement();
		DeliveryQueueDeliveryWizard deliverWizard = new DeliveryQueueDeliveryWizard(checkedDeliveries, selectedDeliveryQueue);
		WizardDialog wizardDialog = new WizardDialog(RCPUtil.getActiveShell(), deliverWizard);

		wizardDialog.open();
	}

	void refreshDeliveryQueues() {

		if (refreshing)
			return;

		refreshing = true;
		Job refreshJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingComposite.refreshDeliveryQueuesJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				final DeliveryQueueConfigModule pqCfMod = getDeliveryQueueConfigModule();

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!isDisposed()) {
							setFaded(true);
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
								if (printQueueCombo.getSelectedElement() != null)
									deliveryTable.setInput(printQueueCombo.getSelectedElement().getPendingDeliveries());
							} else if (!printQueueCombo.getElements().isEmpty()){
								printQueueCombo.setSelection(0);
								if (printQueueCombo.getSelectedElement() != null)
									deliveryTable.setInput(printQueueCombo.getSelectedElement().getPendingDeliveries());
							}

							setFaded(false);
							refreshing = false;
						}
					}
				});
				return Status.OK_STATUS;
			}
		};

		refreshJob.setPriority(Job.SHORT);
		refreshJob.schedule();
	}

	void checkAllDeliveries() {
		deliveryTable.checkAll();
	}

	void uncheckAllDeliveries() {
		deliveryTable.uncheckAll();
	}

//	private StoreManager storeManager;
//
//	private StoreManager getStoreManager() {
//		if (storeManager != null)
//			return storeManager;
//
//		try {
//			storeManager = JFireEjb3Factory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
//			return storeManager;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
}
