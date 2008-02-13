package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.util.Util;

public class PaymentSummaryPage
extends WizardHopPage
{
	private Text paymentText; // TODO we need a Table of course!!!

	public PaymentSummaryPage()
	{
		super(PaymentSummaryPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentSummaryPage.title")); //$NON-NLS-1$
	}

	@Override
	@Implement
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		paymentText = new Text(page, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		paymentText.setFont(new Font(Display.getDefault(), "Courier New", 10, 0)); //$NON-NLS-1$
		paymentText.setLayoutData(new GridData(GridData.FILL_BOTH));

		return page;
	}

	@Override
	public void onShow()
	{
		List<PaymentEntryPage> paymentEntryPages = ((PaymentWizard)getWizard()).getPaymentEntryPages();

		StringBuffer sb = new StringBuffer();
		for (PaymentEntryPage paymentEntryPage : paymentEntryPages) {
			if (Payment.PAYMENT_DIRECTION_INCOMING.equals(paymentEntryPage.getPayment().getPaymentDirection()))
				sb.append(Util.addTrailingChars(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentSummaryPage.paymentDirection_receive"), 15, ' ')); //$NON-NLS-1$
			else if (Payment.PAYMENT_DIRECTION_OUTGOING.equals(paymentEntryPage.getPayment().getPaymentDirection()))
				sb.append(Util.addTrailingChars(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentSummaryPage.paymentDirection_pay"), 15, ' ')); //$NON-NLS-1$
			else
				throw new IllegalStateException("Unknown PaymentDirection: " + paymentEntryPage.getPayment().getPaymentDirection()); //$NON-NLS-1$

			sb.append(' ');
			sb.append(Util.addTrailingChars(paymentEntryPage.getSelectedModeOfPaymentFlavour().getName().getText(), 30, ' '));
			sb.append(' ');
//			sb.append(TableUtil.layoutString(NumberFormatter.formatCurrency(paymentEntryPage.getMaxAmount(), paymentEntryPage.getPayment().getCurrency()), 15, TableUtil.Align.RIGHT));
//			sb.append(' ');
			sb.append(Util.addLeadingChars(NumberFormatter.formatCurrency(paymentEntryPage.getPayment().getAmount(), paymentEntryPage.getPayment().getCurrency()), 15, ' '));
			sb.append('\n');
		}
		paymentText.setText(sb.toString());
	}

	public static PaymentSummaryPage getPaymentSummaryPage(IWizard wizard)
	{
		IWizardPage page = wizard.getStartingPage();
		while (page != null) {
			if (page instanceof PaymentSummaryPage)
				return (PaymentSummaryPage) page;

			page = page.getNextPage();
		}
		return null;
	}
}
