/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class PayAndDeliverReservationAction
//extends SelectionAction
extends AbstractArticleContainerAction
{
	public static final String ID = PayAndDeliverReservationAction.class.getName();

	private ArticleContainerID articleContainerID;

	public PayAndDeliverReservationAction()
	{
		super();
		setId(ID);
		setText("Pay And Deliver...");
		setToolTipText("Pays and delivers the selected reservation");
	}

	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerID();
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_BOTH);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
