package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator;

/**
 * This interface can be implemented to provide custom functionality for handling errors of an implementation of {@link TransferWizard}.
 * It is highly recommended not implement the interface directly but to extend {@link AbstractErrorHandler}.
 *
 * @author daniel[at]nightlabs[dot]de
 */
public interface IErrorHandler
{
	/**
	 *
	 * @param transferWizard the {@link TransferWizard} where this errorHandler belongs to.
	 */
	void initTransferWizard(TransferWizard transferWizard);

	/**
	 * handles the error. All needed data can obtained from the given TransferCoordinator or {@link TransferWizard}.
	 * @return true if the error could be successfully handled or false if not.
	 */
	boolean handleError(TransferCoordinator transferCoordinator);
}
