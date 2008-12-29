/**
 *
 */
package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator;
import org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class DefaultErrorHandler extends AbstractErrorHandler {

//	/* (non-Javadoc)
//	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractErrorHandler#handleError(org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator)
//	 */
//	@Override
//	public boolean handleError(TransferCoordinator transferCoordinator)
//	{
//		ErrorDialog errorDialog = new ErrorDialog(getShell(), transferCoordinator.getPaymentDatas(), transferCoordinator.getDeliveryDatas());
//		if (errorDialog.isFailed()) {
//			errorDialog.open();
//			return false;
//		}
//		return true;
//	}
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractErrorHandler#handleError(org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator)
	 */
	@Override
	public void handleError(TransferCoordinator transferCoordinator)
	{
		ErrorDialog errorDialog = new ErrorDialog(getShell(), transferCoordinator.getPaymentDatas(), transferCoordinator.getDeliveryDatas());
		errorDialog.open();
	}

}
