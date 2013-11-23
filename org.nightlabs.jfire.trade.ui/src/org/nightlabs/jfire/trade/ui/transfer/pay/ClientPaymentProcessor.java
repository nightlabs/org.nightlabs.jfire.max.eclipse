/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.transfer.pay;

import java.util.Set;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentException;
import org.nightlabs.jfire.accounting.pay.PaymentResult;
import org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID;
import org.nightlabs.jfire.trade.ui.transfer.wizard.IPaymentEntryPage;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * Rather than implementing this interface directly, you should extend
 * {@link org.nightlabs.jfire.trade.ui.transfer.pay.AbstractClientPaymentProcessor}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ClientPaymentProcessor
{
	/**
	 * This method is called once before the wizard page is created via {@link #createPaymentWizardPage()}.
	 *
	 * @param clientPaymentProcessorFactory Points back to the {@link ClientPaymentProcessorFactory} that created this processor.
	 */
	void setClientPaymentProcessorFactory(ClientPaymentProcessorFactory clientPaymentProcessorFactory);

	/**
	 * This method is called once before the wizard page is created via {@link #createPaymentWizardPage()}.
	 *
	 * @param partnerID Points to the <tt>LegalEntity</tt> which is about to pay or about to receive a payment.
	 */
	void setPartnerID(AnchorID partnerID);

	/**
	 * This method is called once before the wizard page is created via {@link #createPaymentWizardPage()}.
	 *
	 * @param currency The <tt>Currency</tt> in which the payment shall proceed.
	 */
	void setCurrency(Currency currency);

	/**
	 * This method is called before the wizard page is created via {@link #createPaymentWizardPage()}. Note,
	 * that this method might be called multiple times as it is called again each time the amount to
	 * be paid is changed.
	 *
	 * @param amount The amount to pay.
	 */
	void setAmount(long amount);

	void setPaymentEntryPage(IPaymentEntryPage paymentEntryPage);

	IPaymentEntryPage getPaymentEntryPage();

	long getAmount();
	ClientPaymentProcessorFactory getClientPaymentProcessorFactory();
	Currency getCurrency();
	AnchorID getPartnerID();

	void setPayment(Payment payment);
	Payment getPayment();

	void init();

	String getRequirementCheckKey();

	/**
	 * This method is called after the parameters have been set (i.e. the above setters have been called
	 * - e.g. {@link #setClientPaymentProcessorFactory(ClientPaymentProcessorFactory)}, {@link #setAmount(long)},
	 * {@link #setCurrency(Currency)}, {@link #setPartnerID(AnchorID)}, {@link #setPayment(Payment)}) and before
	 * the creation of the wizard page via {@link #createPaymentWizardPage()}.
	 * <p>
	 * With this method a <tt>ClientPaymentProcessor</tt> might explicitely choose
	 * server side payment implementations. Instead of defining a positive list,
	 * you might consider using {@link #getExcludedServerPaymentProcessorIDs()}.
	 * <p>
	 * If this method returns an instance of <tt>Set</tt> (i.e. not <tt>null</tt>),
	 * the method {@link #getExcludedServerPaymentProcessorIDs()} is ignored.
	 *
	 * @return Your implementation should either return <tt>null</tt>, if all registered
	 *		{@link org.nightlabs.jfire.accounting.pay.ServerPaymentProcessor}s are compatible
	 *		or a {@link Set} of {@link org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID}s.
	 *		If you do not return <tt>null</tt> only those <tt>ServerPaymentProcessor</tt>s will
	 *		be available that are registered and in this <tt>Set</tt>. You might return
	 *		<tt>ServerPaymentProcessorID</tt>s for non-existent (non-installed) processors
	 *		without causing a problem.
	 */
	Set<ServerPaymentProcessorID> getIncludedServerPaymentProcessorIDs();

	/**
	 * This method may be called after the parameters have been set (i.e. the above setters have been called
	 * - e.g. {@link #setClientPaymentProcessorFactory(ClientPaymentProcessorFactory)}, {@link #setAmount(long)},
	 * {@link #setCurrency(Currency)}, {@link #setPartnerID(AnchorID)}, {@link #setPayment(Payment)}) and before
	 * the creation of the wizard page via {@link #createPaymentWizardPage()}.
	 * <p>
	 * Unlike {@link #getIncludedServerPaymentProcessorIDs()}, this method defines
	 * which {@link org.nightlabs.jfire.accounting.pay.ServerPaymentProcessor} are
	 * <b>NOT</b> usable with this <tt>ClientPaymentProcessor</tt>.
	 * <p>
	 * This method will only be called, if {@link #getIncludedServerPaymentProcessorIDs()}
	 * returned <tt>null</tt>.
	 *
	 * @return Your implementation should either return <tt>null</tt>, if all registered
	 *		{@link org.nightlabs.jfire.accounting.pay.ServerPaymentProcessor}s are compatible
	 *		or a {@link Set} of {@link org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID}s
	 *		to exclude certain processors.
	 */
	Set<ServerPaymentProcessorID> getExcludedServerPaymentProcessorIDs();

	/**
 	 * This method is called after the parameters have been set (i.e. the above setters have been called
	 * - e.g. {@link #setClientPaymentProcessorFactory(ClientPaymentProcessorFactory)}, {@link #setAmount(long)},
	 * {@link #setCurrency(Currency)}, {@link #setPartnerID(AnchorID)}, {@link #setPayment(Payment)}) and before
	 * the creation of the wizard page via {@link #createPaymentWizardPage()}.
	 * <p>
	 * You should return the same instance on multiple calls to this method.
	 * </p>
	 *
	 * @return In your implementation of this method, you might return <tt>null</tt> or
	 *		a customized implementation of <tt>PaymentData</tt> to carry specific data
	 *		necessary for your {@link org.nightlabs.jfire.accounting.ModeOfPayment}s.
	 *		This data object can then be populated by wizard pages.
	 */
	PaymentData getPaymentData();

	/**
	 * This method is called in order to create a wizard page for obtaining specific information
	 * necessary for your <code>ClientPaymentProcessor</code> implementation or the
	 * corresponding implementation of <code>ServerPaymentProcessor</code>.
	 *
	 * @return Your implementation of this method should create an implementation
	 *		of <tt>IWizardHopPage</tt> (preferably by extending {@link WizardHopPage})
	 *		to gather additional payment properties from the user.
	 */
	IWizardHopPage createPaymentWizardPage();

//	/**
//	 * This method is called after {@link #createPaymentWizardPage()} and after the
//	 * user has finished the wizard. After the user confirmed the payment,
//	 * the new {@link org.nightlabs.jfire.accounting.Invoice} is created and the
//	 * payment performed.
//	 * <p>
//	 * After this method, the framework calls {@link #setServerPaymentProcessorID(ServerPaymentProcessorID)} and then {@link #payBegin()}.
//	 *
//	 * @param invoiceID The ID of the new <tt>Invoice</tt>.
//	 */
//	void setInvoiceID(InvoiceID invoiceID);
//
//	public InvoiceID getInvoiceID();

	/**
	 * This method is called after {@link #setInvoiceID(InvoiceID)}. It should
	 * approve in case an external payment system is connected and supports approval.
	 * <p>
	 * To find out, whether you need to commit or to rollback, use
	 * {@link #getPayment()}.{@link Payment#isFailed()}.
	 * @return You may return <tt>null</tt>, which will cause automatic creation of
	 *		a <tt>PaymentResult</tt> with <tt>code = </tt>{@link PaymentResult#CODE_APPROVED_NO_EXTERNAL}
	 * 
	 * @throws PaymentException Throw a <tt>PaymentException</tt> or return a "FAILED" <tt>PaymentResult</tt>
	 *		in case of an error. You may throw <tt>RuntimeExceptions</tt>, too, but you
	 *		should use this exception at least for all payment related stuff (not-internal,
	 *		e.g. no contact to telecash terminal).
	 */
	PaymentResult payBegin() throws PaymentException;

	/**
	 * @return You may return <tt>null</tt>, which will cause automatic creation of
	 *		a <tt>PaymentResult</tt> with <tt>code = </tt>{@link PaymentResult#CODE_PAID_NO_EXTERNAL}
	 *
	 * @throws PaymentException Throw a <tt>PaymentException</tt> or return a "FAILED" <tt>PaymentResult</tt>
	 *		in case of an error. You may throw <tt>RuntimeExceptions</tt>, too, but you
	 *		should use this exception at least for all payment related stuff (not-internal,
	 *		e.g. no contact to telecash terminal).
	 */
	PaymentResult payDoWork() throws PaymentException;

	/**
	 * @return You may return <tt>null</tt>, which will cause automatic creation of
	 *		a <tt>PaymentResult</tt> with <tt>code = </tt>{@link PaymentResult#CODE_COMMITTED_NO_EXTERNAL}
	 *		or {@link PaymentResult#CODE_ROLLED_BACK_NO_EXTERNAL}
	 *
	 * @throws PaymentException Throw a <tt>PaymentException</tt> or return a "FAILED" <tt>PaymentResult</tt>
	 *		in case of an error. You may throw <tt>RuntimeExceptions</tt>, too, but you
	 *		should use this exception at least for all payment related stuff (not-internal,
	 *		e.g. no contact to telecash terminal).
	 */
	PaymentResult payEnd() throws PaymentException;

}
