/**
 * 
 */
package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.List;

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
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.trade.ui.resource.Messages;

class SelectTargetDeliveryQueueWizardPage extends WizardHopPage {

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
				AbstractListComposite.getDefaultWidgetStyle(comp), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.SelectTargetDeliveryQueueWizardPage.queueCombo.caption"), labelProvider); //$NON-NLS-1$
		queueCombo.setInput(visibleDeliveryQueues);
		queueCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}
		});

		return comp;
	}

	DeliveryQueue getSelectedDeliveryQueue() {
		return queueCombo.getSelectedElement();
	}

	@Override
	public boolean isPageComplete() {
		return !(queueCombo == null || queueCombo.getSelectedElement() == null);
	}
}