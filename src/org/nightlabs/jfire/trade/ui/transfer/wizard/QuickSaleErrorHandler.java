/**
 *
 */
package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator;
import org.nightlabs.jfire.trade.ui.transfer.error.QuickSaleErrorDialog;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class QuickSaleErrorHandler extends AbstractErrorHandler {

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractErrorHandler#handleError(org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator)
	 */
	@Override
	public boolean handleError(TransferCoordinator transferCoordinator)
	{
		hideWizard();
		QuickSaleErrorDialog dialog = new QuickSaleErrorDialog(getShell(), transferCoordinator.getPaymentDatas(),
				transferCoordinator.getDeliveryDatas(), getTransferWizard());
		dialog.open();
		return dialog.isTransfersSuccessful();
	}

}
