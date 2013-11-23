package org.nightlabs.jfire.trade.ui.transfer.error;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReport;
import org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizardDialog;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.eclipse.compatibility.CompatibleDialogConstants;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.dao.InvoiceDAO;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentResult;
import org.nightlabs.jfire.accounting.pay.id.PayableObjectID;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.DeliveryResult;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticlesWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.QuickSaleErrorHandler;
import org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizard;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

public class ErrorDialog
extends ResizableTitleAreaDialog
{
	private static final int STACK_TRACE_LINE_COUNT = 15;
	private static final int CUSTOM_ELEMENTS_WIDTH_HINT = 300;
	protected static final int SEND_ERROR_REPORT_ID = IDialogConstants.CLIENT_ID + 2;
	protected static final int IGNORE_ID = IDialogConstants.CLIENT_ID + 3;
	protected static final int AUTOMATIC_SOLVE_ID = IDialogConstants.CLIENT_ID + 4;

	private TransferTreeComposite transferTreeComposite;
	private Text errorStackTrace;
	private Button detailsButton;

	private List<PaymentData> paymentDatas;
	private List<DeliveryData> deliveryDatas;

	private boolean failed = false;
	private boolean transfersSuccessful = false;

	private TransferWizard transferWizard;

	public ErrorDialog(Shell parentShell, List<PaymentData> paymentDatas, List<DeliveryData> deliveryDatas, TransferWizard transferWizard)
	{
		super(parentShell, null);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
        | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());

		this.transferWizard = transferWizard;
		this.paymentDatas = paymentDatas;
		this.deliveryDatas = deliveryDatas;
		if (paymentDatas == null && deliveryDatas == null)
			throw new IllegalArgumentException("paymentDatas == null && deliveryDatas == null"); //$NON-NLS-1$


		if (paymentDatas != null) {
			for (PaymentData paymentData : paymentDatas) {
				Payment payment = paymentData.getPayment();
				if (payment.isFailed())
					failed = true;

//				if (!payment.isRolledBack())
//					completeRollback = false;
			}
		}

		if (deliveryDatas != null) {
			for (DeliveryData deliveryData : deliveryDatas) {
				Delivery delivery = deliveryData.getDelivery();
				if (delivery.isFailed())
					failed = true;

//				if (!delivery.isRolledBack())
//					completeRollback = false;
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

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		String title = getWindowTitle();
		newShell.setText(title);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);

		setTitle(getDialogTitle());
		setMessage(getMessage(), IMessageProvider.ERROR);

		Composite sashForm = new XComposite(area, SWT.NONE);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		transferTreeComposite = new TransferTreeComposite(sashForm);
		transferTreeComposite.setInput(paymentDatas, deliveryDatas);
		transferTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
//		transferTreeComposite.getTreeViewer().expandAll();

		createStackTraceText(sashForm);

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

	protected String getWindowTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.message"); //$NON-NLS-1$
	}

	public String getMessage() 
	{
		String message = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.title"); //$NON-NLS-1$
		if (paymentDatas != null && deliveryDatas != null) {
			message = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.message_paymentAndDeliveryFailed"); //$NON-NLS-1$
		}
		else if (paymentDatas != null) {
			message = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.message_paymentFailed"); //$NON-NLS-1$
		}
		else {
			message = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.message_deliveryFailed"); //$NON-NLS-1$
		}
		return message; 
	}
	
	protected String getDialogTitle() {
		String title = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.title"); //$NON-NLS-1$
		if (paymentDatas != null && deliveryDatas != null) {
			title = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.title_paymentAndDeliveryFailed"); //$NON-NLS-1$
		}
		else if (paymentDatas != null) {
			title = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.title_paymentFailed"); //$NON-NLS-1$
		}
		else {
			title = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.title_deliveryFailed"); //$NON-NLS-1$
		}
		return title;
	}

	protected void showStackTrace(boolean visible)
	{
		Point windowSize = getShell().getSize();
		Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		GridData stackTraceGD = ((GridData)errorStackTrace.getLayoutData());
		if(visible) {
			stackTraceGD.heightHint = errorStackTrace.getLineHeight() * STACK_TRACE_LINE_COUNT;
			detailsButton.setText(CompatibleDialogConstants.get().HIDE_DETAILS_LABEL);
			errorStackTrace.setVisible(true);
		} else {
			stackTraceGD.heightHint = 0;
			detailsButton.setText(CompatibleDialogConstants.get().SHOW_DETAILS_LABEL);
			errorStackTrace.setVisible(false);
		}
		Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, AUTOMATIC_SOLVE_ID, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.button.retry.text"), true); //$NON-NLS-1$
		createButton(parent, SEND_ERROR_REPORT_ID, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.button.sendErrorReport.text"), false); //$NON-NLS-1$
//		super.createButtonsForButtonBar(parent);
		createButton(parent, IGNORE_ID, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog.button.ignore.text"), false); //$NON-NLS-1$
		detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, CompatibleDialogConstants.get().SHOW_DETAILS_LABEL, false);
	}

	protected Control createStackTraceText(Composite parent)
	{
		errorStackTrace = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		applyDialogFont(errorStackTrace);
		errorStackTrace.setText(""); //$NON-NLS-1$
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = CUSTOM_ELEMENTS_WIDTH_HINT;
		data.heightHint = 0; //stackTraceText.getLineHeight() * TEXT_LINE_COUNT;
		errorStackTrace.setVisible(false);
		//data.horizontalSpan = 2;
		errorStackTrace.setLayoutData(data);
		return errorStackTrace;
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		switch(buttonId) {
		case AUTOMATIC_SOLVE_ID:
			solveAutomaticPressed();
			break;
		case IDialogConstants.DETAILS_ID:
			showDetailsPressed();
			break;
		case SEND_ERROR_REPORT_ID:
			sendErrorReportPressed();
			break;
		case IGNORE_ID:
			okPressed();
			break;
//		default:
//			super.buttonPressed(buttonId);
		}
	}

	private ErrorReport fillErrorReport(ErrorReport errorReport, List<?> l)
	{
		for (Object o : l) {
			if (o instanceof PaymentData) {
				PaymentData pd = (PaymentData) o;
				errorReport = fillErrorReport(errorReport, pd.getPayment().getPayBeginClientResult());
				errorReport = fillErrorReport(errorReport, pd.getPayment().getPayBeginServerResult());
				errorReport = fillErrorReport(errorReport, pd.getPayment().getPayDoWorkClientResult());
				errorReport = fillErrorReport(errorReport, pd.getPayment().getPayDoWorkServerResult());
				errorReport = fillErrorReport(errorReport, pd.getPayment().getPayEndClientResult());
				errorReport = fillErrorReport(errorReport, pd.getPayment().getPayEndServerResult());
//				fillErrorReport(errorReport, pd.getPayment().getFailurePaymentResult());
			}
			if (o instanceof DeliveryData) {
				DeliveryData dd = (DeliveryData) o;
				errorReport = fillErrorReport(errorReport, dd.getDelivery().getDeliverBeginClientResult());
				errorReport = fillErrorReport(errorReport, dd.getDelivery().getDeliverBeginServerResult());
				errorReport = fillErrorReport(errorReport, dd.getDelivery().getDeliverDoWorkClientResult());
				errorReport = fillErrorReport(errorReport, dd.getDelivery().getDeliverDoWorkServerResult());
				errorReport = fillErrorReport(errorReport, dd.getDelivery().getDeliverEndClientResult());
				errorReport = fillErrorReport(errorReport, dd.getDelivery().getDeliverEndServerResult());
//				fillErrorReport(errorReport, dd.getDelivery().getFailureDeliveryResult());
			}
		}
		return errorReport;
	}

	private ErrorReport fillErrorReport(ErrorReport errorReport, PaymentResult paymentResult)
	{
		if (paymentResult != null) {
			if (paymentResult.getError() != null) {
				if (errorReport == null)
					errorReport = new ErrorReport(paymentResult.getError(), paymentResult.getError());
				else
					errorReport.addThrowablePair(paymentResult.getError(), paymentResult.getError());
			}
			else if (paymentResult.getErrorStackTrace() != null) {
				Exception xxx = new Exception(paymentResult.getErrorStackTrace());
				if (errorReport == null)
					errorReport = new ErrorReport(xxx, xxx);
				else
					errorReport.addThrowablePair(xxx, xxx);
			}
		}
		return errorReport;
	}

	private ErrorReport fillErrorReport(ErrorReport errorReport, DeliveryResult deliveryResult)
	{
		if (deliveryResult != null) {
			if (deliveryResult.getError() != null) {
				if (errorReport == null)
					errorReport = new ErrorReport(deliveryResult.getError(), deliveryResult.getError());
				else
					errorReport.addThrowablePair(deliveryResult.getError(), deliveryResult.getError());
			}
			else if (deliveryResult.getErrorStackTrace() != null) {
				Exception xxx = new Exception(deliveryResult.getErrorStackTrace());
				if (errorReport == null)
					errorReport = new ErrorReport(xxx, xxx);
				else
					errorReport.addThrowablePair(xxx, xxx);
			}
		}
		return errorReport;
	}

	protected List<DeliveryData> getDeliveryDatas() {
		return deliveryDatas;
	}

	protected List<PaymentData> getPaymentDatas() {
		return paymentDatas;
	}


	protected void solveAutomaticPressed()
	{
		okPressed();
		Set<ArticleID> articleIDs = new HashSet<ArticleID>();

		if (getPaymentDatas() != null && (transferWizard.getTransferMode() & TransferWizard.TRANSFER_MODE_PAYMENT) > 0) {
			// add all ArticleIDs from all invoices of all payments
			Set<PayableObjectID> payableObjectIDs = new HashSet<PayableObjectID>();
			for (PaymentData pd : getPaymentDatas()) {
				payableObjectIDs.addAll(pd.getPayment().getPayableObjectIDs());
			}
			Set<InvoiceID> invoiceIDs = CollectionUtil.castSet(payableObjectIDs);
			List<Invoice> invoices = InvoiceDAO.sharedInstance().getInvoices(invoiceIDs, new String[] {FetchPlan.DEFAULT, Invoice.FETCH_GROUP_ARTICLES},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			for (Invoice invoice : invoices) {
				Collection<? extends ArticleID> tmpArticleIDSet = NLJDOHelper.getObjectIDSet(invoice.getArticles());
				articleIDs.addAll(tmpArticleIDSet);
			}
		}
		if (getDeliveryDatas() != null && (transferWizard.getTransferMode() & TransferWizard.TRANSFER_MODE_DELIVERY) > 0) {
			// add all ArticleIDs from all deliveryNotes of all deliveries
			for (DeliveryData dd : getDeliveryDatas()) {
				articleIDs.addAll(dd.getDelivery().getArticleIDs());
			}
		}

		CombiTransferArticlesWizard wizard = new CombiTransferArticlesWizard(articleIDs, transferWizard.getTransferMode());
		wizard.setErrorHandler(new QuickSaleErrorHandler());
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
		this.transfersSuccessful = wizard.isTransfersSuccessful();
	}

	public boolean isTransfersSuccessful() {
		return transfersSuccessful;
	}

	protected void sendErrorReportPressed()
	{
		ErrorReport errorReport = null;
		Object input = transferTreeComposite.getInput();
		if (input instanceof Object[]) {
			Object[] paymentAndDeliveryDatas = (Object[]) input;
			for (Object o : paymentAndDeliveryDatas) {
				if (o instanceof List) {
					errorReport = fillErrorReport(errorReport, (List<?>)o);
				}
			}
		}

		if (errorReport == null) {
			Exception xxx = new IllegalStateException("transferTreeComposite.getInput() did not return any data from which we could create an error report!"); //$NON-NLS-1$
			errorReport = new ErrorReport(xxx, xxx);
		}

		ErrorReportWizardDialog dlg = new ErrorReportWizardDialog(errorReport);
		okPressed();
		dlg.open();
	}

	protected void showDetailsPressed()
	{
		boolean show = ((GridData)errorStackTrace.getLayoutData()).heightHint == 0;
		showStackTrace(show);
	}
}
