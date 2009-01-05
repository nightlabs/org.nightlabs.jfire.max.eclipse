/**
 *
 */
package org.nightlabs.jfire.trade.ui.transfer.error;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizard;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class QuickSaleErrorDialog
extends ErrorDialog
{
	protected static final int MANUAL_SOLVE_ID = IDialogConstants.CLIENT_ID + 5;

	/**
	 * @param parentShell
	 * @param paymentDatas
	 * @param deliveryDatas
	 */
	public QuickSaleErrorDialog(Shell parentShell,
			List<PaymentData> paymentDatas, List<DeliveryData> deliveryDatas, TransferWizard transferWizard) {
		super(parentShell, paymentDatas, deliveryDatas, transferWizard);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, MANUAL_SOLVE_ID, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.QuickSaleErrorDialog.button.solveManual.text"), false); //$NON-NLS-1$
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		switch(buttonId) {
		case MANUAL_SOLVE_ID:
			solveManualPressed();
			break;
		default:
			super.buttonPressed(buttonId);
		}
	}

	protected void solveManualPressed()
	{
		okPressed();
		if (getPaymentDatas() != null) {
			for (PaymentData pd : getPaymentDatas()) {
				for (InvoiceID invoiceID : pd.getPayment().getInvoiceIDs()) {
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(
								new ArticleContainerEditorInput(invoiceID), ArticleContainerEditor.ID_EDITOR);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		if (getDeliveryDatas() != null) {
			for (DeliveryData dd : getDeliveryDatas()) {
				for (DeliveryNoteID deliveryNoteID : dd.getDelivery().getDeliveryNoteIDs()) {
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(
								new ArticleContainerEditorInput(deliveryNoteID), ArticleContainerEditor.ID_EDITOR);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
