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

package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.Set;

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.DeliveryException;
import org.nightlabs.jfire.store.deliver.DeliveryResult;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.transfer.wizard.IDeliveryEntryPage;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * Just like payment processors, there may be many delivery processors involved in
 * one delivery process. Unlike with payments, this is not dependent on the user
 * splitting a delivery, but it is always forced, if there are miscellaneous
 * {@link org.nightlabs.jfire.store.ProductType}s with
 * different {@link org.nightlabs.jfire.store.deliver.DeliveryConfiguration}s involved.
 * Hence, if the <tt>Article</tt>s have varying <tt>ClientDeliveryProcessor</tt>s
 * or <tt>ServerDeliveryProcessor</tt>s, they get grouped into multiple groups (each
 * processed in a separate delivery process).
 * <p>
 * If the user chooses the same <tt>ClientDeliveryProcessor</tt> and the same
 * <tt>ServerDeliveryProcessor</tt> for multiple groups, they will be processed
 * together (with the same <tt>DeliveryData</tt> and the same <tt>Delivery</tt>).
 * </p>
 * <p>
 * It is recommended not to implement this interface directly, but to extend
 * {@link AbstractClientDeliveryProcessor} instead.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ClientDeliveryProcessor
{
	/**
	 * This method is called before {@link #init()}.
	 *
	 * @param clientDeliveryProcessorFactory Points back to the {@link ClientDeliveryProcessorFactory} that created this processor.
	 */
	void setClientDeliveryProcessorFactory(ClientDeliveryProcessorFactory clientDeliveryProcessorFactory);

	/**
	 * This method is called before {@link #init()}.
	 *
	 * @param customerID Points to the <tt>LegalEntity</tt> which is about to deliver.
	 */
	void setCustomerID(AnchorID customerID);


	void setDeliveryEntryPage(IDeliveryEntryPage deliveryEntryPage);
	IDeliveryEntryPage getDeliveryEntryPage();


	ClientDeliveryProcessorFactory getClientDeliveryProcessorFactory();
	AnchorID getCustomerID();

	void setDelivery(Delivery delivery);
	Delivery getDelivery();

	/**
	 */
	void init();

	String getRequirementCheckKey();

	/**
	 * This method is called after {@link #init()}.
	 * <p>
	 * With this method a <tt>ClientDeliveryProcessor</tt> might explicitely choose
	 * server side delivery implementations. Instead of defining a positive list,
	 * you might consider using {@link #getExcludedServerDeliveryProcessorIDs()}.
	 * <p>
	 * If this method returns an instance of <tt>Set</tt> (i.e. not <tt>null</tt>),
	 * the method {@link #getExcludedServerDeliveryProcessorIDs()} is ignored.
	 *
	 * @return Your implementation should either return <tt>null</tt>, if all registered
	 *		{@link org.nightlabs.jfire.store.deliver.ServerDeliveryProcessor}s are compatible
	 *		or a {@link Set} of {@link org.nightlabs.jfire.store.deliver.id.ServerDeliveryProcessorID}s.
	 *		If you do not return <tt>null</tt> only those <tt>ServerDeliveryProcessor</tt>s will
	 *		be available that are registered and in this <tt>Set</tt>. You might return
	 *		<tt>ServerDeliveryProcessorID</tt>s for non-existent (non-installed) processors
	 *		without causing a problem.
	 */
	Set getIncludedServerDeliveryProcessorIDs();

	/**
	 * This method is called after {@link #init()}.
	 * <p>
	 * Unlike {@link #getIncludedServerDeliveryProcessorIDs()}, this method defines
	 * which {@link org.nightlabs.jfire.store.deliver.ServerDeliveryProcessor} are
	 * <b>NOT</b> usable with this <tt>ClientDeliveryProcessor</tt>.
	 * <p>
	 * This method will only be called, if {@link #getIncludedServerDeliveryProcessorIDs()}
	 * returned <tt>null</tt>.
	 *
	 * @return Your implementation should either return <tt>null</tt>, if all registered
	 *		{@link org.nightlabs.jfire.store.deliver.ServerDeliveryProcessor}s are compatible
	 *		or a {@link Set} of {@link org.nightlabs.jfire.store.deliver.id.ServerDeliveryProcessorID}s
	 *		to exclude certain processors.
	 */
	Set getExcludedServerDeliveryProcessorIDs();

	/**
	 * This method is called after {@link #init()}. You should not replace
	 * the instance of <tt>DeliveryData</tt> that you returned at that time, because
	 * this instance will be passed to the <tt>DeliveryWizard</tt> by
	 * {@link DeliveryWizard#setDeliveryData(DeliveryData)}.
	 *
	 * @return In your implementation of this method, you might return <tt>null</tt> or
	 *		a customized implementation of <tt>DeliveryData</tt> to carry specific data
	 *		necessary for your {@link org.nightlabs.jfire.store.ModeOfDelivery}s.
	 *		This data object can then be populated by wizard pages.
	 */
	DeliveryData getDeliveryData();

	/**
	 * This method is called after {@link #init()}.
	 *
	 * @return Your implementation of this method should create an implementation
	 *		of <tt>IDynamicPathWizardPage</tt> to gather additional delivery properties from
	 *		the user.
	 */
	IWizardHopPage createDeliveryWizardPage();

	/**
	 * This method is called after {@link #createDeliveryWizardPage()} and after the
	 * user has finished the wizard. After the user confirmed the delivery,
	 * the new {@link org.nightlabs.jfire.store.DeliveryNote} is created and the
	 * delivery performed.
	 * <p>
	 * After this method, the framework calls {@link #setServerDeliveryProcessorID(ServerDeliveryProcessorID)} and then {@link #deliverBegin()}.
	 *
	 * @param deliveryNoteID The ID of the new <tt>DeliveryNote</tt>
	 */
	void setDeliveryNoteID(DeliveryNoteID deliveryNoteID);

	public DeliveryNoteID getDeliveryNoteID();

	/**
	 * This method is called after {@link #setDeliveryNoteID(DeliveryNoteID)}. It should
	 * approve in case an external delivery system is connected and supports approval.
	 * <p>
	 * To find out, whether you need to commit or to rollback, use
	 * {@link #getDelivery()}.{@link Delivery#isFailed()}.
	 * @return You may return <tt>null</tt>, which will cause automatic creation of
	 *		a <tt>DeliveryResult</tt> with <tt>code = </tt>{@link DeliveryResult#CODE_APPROVED_NO_EXTERNAL}
	 * 
	 * @throws DeliveryException TODO
	 */
	DeliveryResult deliverBegin() throws DeliveryException;

	/**
	 * @return You may return <tt>null</tt>, which will cause automatic creation of
	 *		a <tt>DeliveryResult</tt> with <tt>code = </tt>{@link DeliveryResult#CODE_DELIVERED_NO_EXTERNAL}
	 *
	 * @throws PaymentException Throw a <tt>PaymentException</tt> or return a "FAILED" <tt>PaymentResult</tt>
	 *		in case of an error. You may throw <tt>RuntimeExceptions</tt>, too, but you
	 *		should use this exception at least for all payment related stuff (not-internal,
	 *		e.g. no contact to telecash terminal).
	 */
	DeliveryResult deliverDoWork() throws DeliveryException;

	/**
	 * @return You may return <tt>null</tt>, which will cause automatic creation of
	 *		a <tt>DeliveryResult</tt> with <tt>code = </tt>{@link DeliveryResult#CODE_COMMITTED_NO_EXTERNAL}
	 *		or {@link DeliveryResult#CODE_ROLLED_BACK_NO_EXTERNAL}
	 *
	 * @throws PaymentException Throw a <tt>PaymentException</tt> or return a "FAILED" <tt>PaymentResult</tt>
	 *		in case of an error. You may throw <tt>RuntimeExceptions</tt>, too, but you
	 *		should use this exception at least for all payment related stuff (not-internal,
	 *		e.g. no contact to telecash terminal).
	 */
	DeliveryResult deliverEnd() throws DeliveryException;
}
