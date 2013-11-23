package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
//	private XComboComposite<DeliveryQueue> queueCombo;
	private DeliveryQueueSelectionTable queueTable;
	private List<DeliveryQueue> visibleDeliveryQueues;

	public SelectTargetDeliveryQueueWizardPage(List<DeliveryQueue> visibleDeliveryQueues) {
		super(SelectTargetDeliveryQueueWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.description")); //$NON-NLS-1$
		this.visibleDeliveryQueues = visibleDeliveryQueues;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		
		Label lbl = new Label(comp, SWT.WRAP);
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, lbl);
		lbl.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.label")); //$NON-NLS-1$
		
		queueTable = new DeliveryQueueSelectionTable(comp);
		
		Collections.sort(visibleDeliveryQueues, new Comparator<DeliveryQueue>() {
			@Override
			public int compare(DeliveryQueue o1, DeliveryQueue o2) {
				return o1.getName().getText().compareTo(o2.getName().getText());
			}
		});
		
		queueTable.setInput(visibleDeliveryQueues);
		
		final DeliveryQueueConfigModule configModule = Config.sharedInstance().createConfigModule(DeliveryQueueConfigModule.class);
		queueTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				getContainer().updateButtons();
				configModule.setLastUsedDeliveryQueueId((DeliveryQueueID) JDOHelper.getObjectId(queueTable.getFirstSelectedElement()));
			}
		});
		
		DeliveryQueue lastSelectedDeliveryQueue = null;
		for (DeliveryQueue dq : visibleDeliveryQueues) {
			if (dq.getObjectID().equals(configModule.getLastUsedDeliveryQueueId()))
				lastSelectedDeliveryQueue = dq;
		}
		
		if (lastSelectedDeliveryQueue != null)
			queueTable.setSelectedElements(Collections.singleton(lastSelectedDeliveryQueue));

		return comp;
	}
	
	DeliveryQueue getSelectedDeliveryQueue() {
		return queueTable.getFirstSelectedElement();
	}

	@Override
	public boolean isPageComplete() {
		return !(queueTable == null || queueTable.getFirstSelectedElement() == null);
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