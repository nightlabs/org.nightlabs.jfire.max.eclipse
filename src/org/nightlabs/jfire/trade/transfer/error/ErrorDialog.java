package org.nightlabs.jfire.trade.transfer.error;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.trade.resource.Messages;

public class ErrorDialog
		extends Dialog
{
	private TransferTreeComposite transferTreeComposite;
	private Text errorStackTrace;

	private List<PaymentData> paymentDatas;
	private List<DeliveryData> deliveryDatas;

	private boolean failed = false;
	private boolean completeRollback = true;

	public ErrorDialog(Shell parentShell, List<PaymentData> paymentDatas, List<DeliveryData> deliveryDatas)
	{
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
        | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());

		this.paymentDatas = paymentDatas;
		this.deliveryDatas = deliveryDatas;
		if (paymentDatas == null && deliveryDatas == null)
			throw new IllegalArgumentException("paymentDatas == null && deliveryDatas == null"); //$NON-NLS-1$

		
		if (paymentDatas != null) {
			for (PaymentData paymentData : paymentDatas) {
				Payment payment = paymentData.getPayment();
				if (payment.isFailed())
					failed = true;

				if (!payment.isRolledBack())
					completeRollback = false;
			}
		}

		if (deliveryDatas != null) {
			for (DeliveryData deliveryData : deliveryDatas) {
				Delivery delivery = deliveryData.getDelivery();
				if (delivery.isFailed())
					failed = true;

				if (!delivery.isRolledBack())
					completeRollback = false;
			}
		}

		if (!failed) {
			if (paymentDatas != null) {
				for (PaymentData paymentData : paymentDatas) {
					Payment payment = paymentData.getPayment();
					if (payment.getPayBeginClientResult() == null ||
							payment.getPayBeginServerResult() == null ||
							payment.getPayDoWorkClientResult() == null ||
							payment.getPayDoWorkServerResult() == null ||
							payment.getPayEndClientResult() == null ||
							payment.getPayEndServerResult() == null)
						throw new IllegalStateException("At least one of the pay-results is missing!!!"); //$NON-NLS-1$
				}
			}

			if (deliveryDatas != null) {
				for (DeliveryData deliveryData : deliveryDatas) {
					Delivery delivery = deliveryData.getDelivery();
					if (delivery.getDeliverBeginClientResult() == null ||
							delivery.getDeliverBeginServerResult() == null ||
							delivery.getDeliverDoWorkClientResult() == null ||
							delivery.getDeliverDoWorkServerResult() == null ||
							delivery.getDeliverEndClientResult() == null ||
							delivery.getDeliverEndServerResult() == null)
						throw new IllegalStateException("At least one of the deliver-results is missing!!!"); //$NON-NLS-1$
				}
			}
		} // if (!failed) {
	}

	public boolean isFailed()
	{
		return failed;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);

		newShell.setSize(800, 600);

		String title;
		if (paymentDatas != null && deliveryDatas != null) {
			title = Messages.getString("org.nightlabs.jfire.trade.transfer.error.ErrorDialog.title_paymentAndDeliveryFailed"); //$NON-NLS-1$
		}
		else if (paymentDatas != null) {
			title = Messages.getString("org.nightlabs.jfire.trade.transfer.error.ErrorDialog.title_paymentFailed"); //$NON-NLS-1$
		}
		else {
			title = Messages.getString("org.nightlabs.jfire.trade.transfer.error.ErrorDialog.title_deliveryFailed"); //$NON-NLS-1$
		}

		newShell.setText(title);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);

		SashForm sashForm = new SashForm(area, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		transferTreeComposite = new TransferTreeComposite(sashForm);
		transferTreeComposite.setInput(paymentDatas, deliveryDatas);
		transferTreeComposite.getTreeViewer().expandAll();

		errorStackTrace = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		errorStackTrace.setLayoutData(new GridData(GridData.FILL_BOTH));

		transferTreeComposite.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (event.getSelection().isEmpty()) {
					errorStackTrace.setText(""); //$NON-NLS-1$
					return;
				}

				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				Object o = sel.getFirstElement();
				String stackTrace = null;
				if (o instanceof PaymentResultTreeNode) {
					stackTrace = ((PaymentResultTreeNode)o).getPaymentResult().getErrorStackTrace();
				}
				else if (o instanceof DeliveryResultTreeNode) {
					stackTrace = ((DeliveryResultTreeNode)o).getDeliveryResult().getErrorStackTrace();
				}

				if (stackTrace == null)
					stackTrace = ""; //$NON-NLS-1$

				errorStackTrace.setText(stackTrace);
			}
		});

		return area;
	}
}
