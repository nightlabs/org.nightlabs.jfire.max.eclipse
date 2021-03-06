package org.nightlabs.jfire.trade.ui.overview.invoice.action;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;


/**
 * @author Fitas. [at] NightLabs [dot] de
 *
 */
public class PayInvoiceAction extends AbstractArticleContainerAction
{
	public static final String ID = PayInvoiceAction.class.getName();
	
	public PayInvoiceAction()
	{
		super();
		setId(ID);
	}
	
	@Override
	public boolean calculateEnabled() {
		if (!super.calculateEnabled())
			return false;		
		ArticleContainer articleContainer = getArticleContainer(new String[] {
				FetchPlan.DEFAULT,
				Invoice.FETCH_GROUP_CUSTOMER_ID,
				Invoice.FETCH_GROUP_PRICE,
				Invoice.FETCH_GROUP_INVOICE_LOCAL
			});		
		if (!(articleContainer instanceof Invoice))
			return true;
		return ((Invoice)articleContainer).getInvoiceLocal().getAmountToPay() != 0;
	}
	
	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerID();
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_PAYMENT);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}


	@Override
	public boolean calculateVisible() {
		return true;
	}
}	