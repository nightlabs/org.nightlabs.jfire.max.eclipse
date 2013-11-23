/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class ReservationWizardDialog
extends DynamicPathWizardDialog
{
	public static final int RESERVATION_ID = IDialogConstants.CLIENT_ID + 1;
	private static final Logger logger = Logger.getLogger(ReservationWizardDialog.class);

	private Button reservationButton;

	/**
	 * @param wizard
	 */
	public ReservationWizardDialog(ReservationPaymentDeliveryWizard wizard) {
		super(wizard);
	}

	/**
	 * @param shell
	 * @param wizard
	 */
	public ReservationWizardDialog(Shell shell, ReservationPaymentDeliveryWizard wizard) {
		super(shell, wizard);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		reservationButton = createButton(parent, RESERVATION_ID, Messages.getString("org.nightlabs.jfire.trade.ui.reserve.ReservationWizardDialog.button.reservation.text"), false); //$NON-NLS-1$
		super.createButtonsForButtonBar(parent);
	}

	@Override
	public void updateButtons()
	{
		boolean canReserve = getWizard().canReserve();
		reservationButton.setEnabled(canReserve);
		super.updateButtons();
		if (logger.isDebugEnabled()) {
			logger.debug("canReserve = "+canReserve); //$NON-NLS-1$
		}
	}

	@Override
	protected ReservationPaymentDeliveryWizard getWizard() {
		return (ReservationPaymentDeliveryWizard) super.getWizard();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == RESERVATION_ID) {
			setReturnCode(Window.OK);
			close();
		}
	}
}
