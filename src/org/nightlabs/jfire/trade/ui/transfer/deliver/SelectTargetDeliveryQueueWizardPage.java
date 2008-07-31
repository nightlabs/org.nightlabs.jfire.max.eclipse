package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.config.Config;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.store.deliver.id.DeliveryQueueID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

class SelectTargetDeliveryQueueWizardPage
extends WizardHopPage
{
	private XComboComposite<DeliveryQueue> queueCombo;
	private List<DeliveryQueue> visibleDeliveryQueues;

	public SelectTargetDeliveryQueueWizardPage(List<DeliveryQueue> visibleDeliveryQueues) {
		super(SelectTargetDeliveryQueueWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.description")); //$NON-NLS-1$
		this.visibleDeliveryQueues = visibleDeliveryQueues;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		LabelProvider labelProvider = new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DeliveryQueue) {
					DeliveryQueue deliveryQueue = (DeliveryQueue) element;
					return deliveryQueue.getName().getText();
				}
				return ""; //$NON-NLS-1$
			}
		};
		queueCombo = new XComboComposite<DeliveryQueue>(comp,
				AbstractListComposite.getDefaultWidgetStyle(comp),
				Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.queueCombo.caption"), //$NON-NLS-1$
				labelProvider);
		Collections.sort(visibleDeliveryQueues, new Comparator<DeliveryQueue>() {
			@Override
			public int compare(DeliveryQueue o1, DeliveryQueue o2) {
				return o1.getName().getText().compareTo(o2.getName().getText());
			}
		});
		
		queueCombo.setInput(visibleDeliveryQueues);
		final DeliveryQueueConfigModule configModule = Config.sharedInstance().createConfigModule(DeliveryQueueConfigModule.class);
		queueCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
				configModule.setLastUsedDeliveryQueueId((DeliveryQueueID) JDOHelper.getObjectId(queueCombo.getSelectedElement()));
			}
		});
		
		DeliveryQueue lastSelectedDeliveryQueue = null;
		for (DeliveryQueue dq : visibleDeliveryQueues) {
			if (dq.getObjectID().equals(configModule.getLastUsedDeliveryQueueId()))
				lastSelectedDeliveryQueue = dq;
		}
		if (lastSelectedDeliveryQueue != null)
			queueCombo.setSelection(lastSelectedDeliveryQueue);

		return comp;
	}

	protected DeliveryQueue getSelectedDeliveryQueue() {
		return queueCombo.getSelectedElement();
	}

	@Override
	public boolean isPageComplete() {
		return !(queueCombo == null || queueCombo.getSelectedElement() == null);
	}
	
	@Override
	public void onNext() {
		super.onNext();
	}
	
	@Override
	public void onFinish() {
		super.onFinish();
		onNext();
	}
}