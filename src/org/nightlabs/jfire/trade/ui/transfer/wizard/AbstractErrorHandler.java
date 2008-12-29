/**
 *
 */
package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator;

/**
 * Abstract base implementation for the interface {@link IErrorHandler}.
 * It is highly recommended not implement the interface {@link IErrorHandler} directly but to extend this class.
 *
 * @author daniel[at]nightlabs[dot]de
 *
 */
public abstract class AbstractErrorHandler implements IErrorHandler
{
	private TransferWizard transferWizard;

	protected TransferWizard getTransferWizard() {
		return transferWizard;
	}

	protected Shell getShell() {
		return transferWizard.getContainer().getShell();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.IErrorHandler#initTransferWizard(org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizard)
	 */
	@Override
	public void initTransferWizard(TransferWizard transferWizard) {
		this.transferWizard = transferWizard;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.IErrorHandler#handleError(org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator)
	 */
	@Override
//	public abstract boolean handleError(TransferCoordinator transferCoordinator);
	public abstract void handleError(TransferCoordinator transferCoordinator);
}
