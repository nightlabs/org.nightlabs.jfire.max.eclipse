package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.action;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.dashboard.ui.action.AbstractDashboardTableAction;
import org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.InvoiceTableItem;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.transfer.PayAction;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;

/**
 * @author abieber
 *
 */
public class PayInvoiceAction extends
		AbstractDashboardTableAction<InvoiceTableItem> {

	public PayInvoiceAction() {
		setId(PayInvoiceAction.class.getName());
		setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.action.PayInvoiceAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				TradePlugin.getDefault(), PayAction.class));
	}
	
	@Override
	public boolean calculateEnabled() {
		if (!super.calculateEnabled())
			return false;		
		InvoiceTableItem tableItem = getFirstSelectedTableItem();
		if (tableItem != null) {
			return tableItem.getAmountToPay() != 0;
		}
		return true;
	}
	
	@Override
	public void run()
	{
		InvoiceTableItem tableItem = getFirstSelectedTableItem();
		if (tableItem != null) {
			CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
					tableItem.getInvoiceID(),
					AbstractCombiTransferWizard.TRANSFER_MODE_PAYMENT);
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
			dialog.open();
		}
	}
}
