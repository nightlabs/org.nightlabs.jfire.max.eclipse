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

package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractCombiTransferWizard
extends DynamicPathWizard
implements CombiTransferWizard
{
	private byte transferMode;

	/**
	 * This is set after in perform finish to indicate
	 * whether the transfers where successful.
	 * A specialized dialog is shown then, but the wizard will close.
	 */
	private boolean transfersSuccessful;

	public boolean isDeliveryEnabled()
	{
		return ((transferMode & TRANSFER_MODE_DELIVERY) != 0);
	}

	public boolean isPaymentEnabled()
	{
		return ((transferMode & TRANSFER_MODE_PAYMENT) != 0);
	}

	/**
	 * @return Returns the transferMode.
	 */
	public byte getTransferMode()
	{
		return transferMode;
	}

	/**
	 * @param transferMode One of {@link #TRANSFER_MODE_PAYMENT},
	 *		{@link #TRANSFER_MODE_DELIVERY} or {@link #TRANSFER_MODE_BOTH}.
	 */
	public AbstractCombiTransferWizard(byte transferMode)
	{
		setTransferMode(transferMode);
		setNeedsProgressMonitor(true);
	}

//	/**
//	 * For subclasses only, remember to call {@link #setTransferMode(byte)} after the constructor
//	 */
//	protected AbstractCombiTransferWizard() {
//		super();
//	}

	protected void setTransferMode(byte transferMode)
	{
		if (transferMode != TRANSFER_MODE_PAYMENT &&
				transferMode != TRANSFER_MODE_DELIVERY &&
				transferMode != TRANSFER_MODE_BOTH)
			throw new IllegalArgumentException("Invalid transferMode! Must be one of: TRANSFER_MODE_PAYMENT, TRANSFER_MODE_DELIVERY, TRANSFER_MODE_BOTH"); //$NON-NLS-1$

		this.transferMode = transferMode;
	}

	private Side side;

	@Override
	public Side getSide() {
		if (side == null)
			throw new IllegalStateException("Side was not determined yet"); //$NON-NLS-1$
		return side;
	}

	protected void setSide(Side side) {
		this.side = side;
	}

	private List<PaymentEntryPage> paymentEntryPages = null;

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentWizard#getPaymentEntryPages()
	 */
	public List<PaymentEntryPage> getPaymentEntryPages()
	{
		if (paymentEntryPages == null && (transferMode & TRANSFER_MODE_PAYMENT) != 0) {
			try {
				paymentEntryPages = new ArrayList<PaymentEntryPage>();
				PaymentEntryPage pep = new PaymentEntryPage(
						new Payment(IDGenerator.getOrganisationID(), IDGenerator.nextID(Payment.class)));
				paymentEntryPages.add(pep);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return paymentEntryPages;
	}

	private List<DeliveryEntryPage> deliveryEntryPages = null;

	public List<DeliveryEntryPage> getDeliveryEntryPages()
	{
		if (deliveryEntryPages == null && (transferMode & TRANSFER_MODE_DELIVERY) != 0) {
			try {
				deliveryEntryPages = TransferWizardUtil.createDeliveryEntryPages(this);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return deliveryEntryPages;
	}

	@Override
	public void addPages()
	{
		super.addPages();

//		if (paymentDirection == null)
//			throw new IllegalStateException("paymentDirection has not been set! You must set it in your implementation's constructor!");

		if (getPaymentEntryPages() != null) {
			long amount = getTotalAmount();
			String paymentDirection;
			if (amount >= 0 ^ Side.Vendor.equals(getSide()))
				paymentDirection = Payment.PAYMENT_DIRECTION_OUTGOING;
			else
				paymentDirection = Payment.PAYMENT_DIRECTION_INCOMING;

			PaymentEntryPage firstPaymentEntryPage = null;
			for (Iterator<PaymentEntryPage> it = getPaymentEntryPages().iterator(); it.hasNext(); ) {
				PaymentEntryPage page = it.next();

				page.getPayment().setPaymentDirection(paymentDirection);

				if (firstPaymentEntryPage == null)
					firstPaymentEntryPage = page;

				addPage(page);
			}

			amount = Math.abs(amount);
			firstPaymentEntryPage.getPaymentWizardHop().setMaxAmount(amount);
			firstPaymentEntryPage.getPaymentWizardHop().setAmount(amount);
		}

		if (getDeliveryEntryPages() != null) {
			for (Iterator<DeliveryEntryPage> it = getDeliveryEntryPages().iterator(); it.hasNext(); ) {
				DeliveryEntryPage page = it.next();
				if (getPageCount() > 0) // we add all except the first page as dynamic pages to allow the first PaymentEntryPage to add other PaymentEntryPages BEFORE the DeliveryEntryPages
					addDynamicWizardPage(page);
				else
					addPage(page);
			}
		}
	}

//	private String paymentDirection = null;
//
//	protected String getPaymentDirection()
//	{
//		return paymentDirection;
//	}
//	protected void setPaymentDirection(String paymentDirection)
//	{
//		this.paymentDirection = paymentDirection;
//	}

	private Currency currency;
	private long totalAmount;

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentWizard#getCurrency()
	 */
	public Currency getCurrency()
	{
		return currency;
	}
	/**
	 * @param currency The currency to set.
	 */
	protected void setCurrency(Currency currency)
	{
		this.currency = currency;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentWizard#getTotalAmount()
	 */
	public long getTotalAmount()
	{
		return totalAmount;
	}
	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentWizard#setTotalAmount(long)
	 */
	public void setTotalAmount(long totalAmount)
	{
//		if (totalAmount < 0)
//			throw new IllegalArgumentException("totalAmount = "+totalAmount+" < 0! Must be >= 0");
		this.totalAmount = totalAmount;
	}

	private AnchorID customerID;
	private Collection<CustomerGroupID> customerGroupIDs = new HashSet<CustomerGroupID>();

	@Override
	public AnchorID getPartnerID()
	{
		return customerID;
	}
	/**
	 * @param customerID The customerID to set.
	 */
	protected void setCustomerID(AnchorID customerID)
	{
		this.customerID = customerID;
	}

	public AnchorID getCustomerID() {
		return customerID;
	}

	@Override
	public Collection<CustomerGroupID> getCustomerGroupIDs()
	{
		return Collections.unmodifiableCollection(customerGroupIDs);
	}
	public void addCustomerGroupID(CustomerGroupID customerGroupID)
	{
		if (!customerGroupIDs.contains(customerGroupID))
			customerGroupIDs.add(customerGroupID);
	}
	public void removeCustomerGroupID(CustomerGroupID customerGroupID)
	{
		customerGroupIDs.remove(customerGroupID);
	}
	public void clearCustomerGroupIDs()
	{
		customerGroupIDs.clear();
	}

	@Override
	public void addDynamicWizardPage(int index, IWizardPage page)
	{
		super.addDynamicWizardPage(index, page);

		if (page instanceof PaymentEntryPage) {
			if (paymentEntryPages == null)
				paymentEntryPages = new ArrayList<PaymentEntryPage>();
			paymentEntryPages.add((PaymentEntryPage) page);
		}
	}

	@Override
	public void addDynamicWizardPage(IWizardPage page)
	{
		super.addDynamicWizardPage(page);

		if (page instanceof PaymentEntryPage) {
			if (paymentEntryPages == null)
				paymentEntryPages = new ArrayList<PaymentEntryPage>();
			paymentEntryPages.add((PaymentEntryPage) page);
		}
	}

	@Override
	public void removeDynamicWizardPage(IWizardPage page)
	{
		if (page instanceof PaymentEntryPage)
			paymentEntryPages.remove(page);

		super.removeDynamicWizardPage(page);
	}

	@Override
	public void removeDynamicWizardPage(int index)
	{
		IDynamicPathWizardPage page = getDynamicWizardPage(index);

		if (page instanceof PaymentEntryPage)
			paymentEntryPages.remove(page);

		super.removeDynamicWizardPage(index);
	}

	@Override
	public void removeAllDynamicWizardPages()
	{
		throw new UnsupportedOperationException("This method is not supported!"); //$NON-NLS-1$
	}

	/**
	 * Calling this method triggers asynchronous reloading of the modes of payment/delivery based on the {@link CustomerGroupID}s set in this wizard.
	 */
	public void reloadPaymentDeliveryModes() {
		for (PaymentEntryPage page : paymentEntryPages)
			page.loadModeOfPayments();

		for (DeliveryEntryPage page : deliveryEntryPages)
			page.loadModeOfDeliveries();
	}

	private IErrorHandler errorHandler;

	@Override
	public void setErrorHandler(IErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		errorHandler.initTransferWizard(this);
	}

	@Override
	/**
	 * Never returns null, but if no error handler is set the DefaultErrorHandler is used.
	 */
	public IErrorHandler getErrorHandler()
	{
		if (errorHandler == null) {
			setErrorHandler(new DefaultErrorHandler());
		}
		return errorHandler;
	}

	protected void setTransfersSuccessful(boolean transfersSuccessful) {
		this.transfersSuccessful = transfersSuccessful;
	}

	/**
	 * This is set before the wizard closes and indicates
	 * whether the transfers could be successfully created.
	 * In case of an error the wizard will show an error
	 * but still close, then this flag will be <code>false</code>.
	 * @return <code>true</code> if the transfers have been created successfully, <code>false</code> otherwise.
	 */
	public boolean isTransfersSuccessful() {
		return transfersSuccessful;
	}
}
