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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.CustomerGroupID;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface DeliveryWizard
extends IDynamicPathWizard, TransferWizard
{
//	public static final String SIDE_VENDOR = "vendor";
//	public static final String SIDE_CUSTOMER = "customer";
//
//	/**
//	 * This wizard can be either used on the vendor or on the customer side. If it is used
//	 * on the vendor side, it means that the local organisation is the vendor. The customer side
//	 * specifies that the local organisation is the customer. This is e.g. the case when the
//	 * local organisation has received an invoice and wants to book the payment (or even
//	 * DO the payment via electronic money transfer).
//	 *
//	 * @return Returns either {@link #SIDE_VENDOR} or {@link #SIDE_CUSTOMER}.
//	 */
//	String getSide();
//
//	/**
//	 * @return Returns an <tt>AnchorID</tt> which references a {@link org.nightlabs.jfire.trade.ui.LegalEntity}.
//	 */
//	AnchorID getPartnerID();

	/**
	 * Hint: Delegate to {@link TransferWizardUtil#getProductTypeIDs(Collection)}.
	 *
	 * @return Returns a <tt>Collection</tt> of
	 *		{@link org.nightlabs.jfire.store.id.ProductTypeID}.
	 */
	Collection getProductTypeIDs();

	/**
	 * Hint: Delegate to {@link TransferWizardUtil#getArticles(Collection, Set, boolean)}.
	 *
	 * @param productTypeIDs Instances of {@link org.nightlabs.jfire.store.ProductType}.
	 * @param reversing This method returns only those {@link Article}s where <code>reversing == </code>{@link Article#isReversing()}.
	 * @return Returns instances of {@link org.nightlabs.jfire.trade.ui.Article} or <code>null</code>.
	 */
	List getArticles(Set productTypeIDs, boolean reversing);

//	/**
//	 * Hint: Delegate to {@link TransferWizardUtil#getProductIDs(Collection, Set)}.
//	 *
//	 * @param productTypeIDs Instances of
//	 *		{@link org.nightlabs.jfire.store.id.ProductTypeID}.
//	 *
//	 * @return Returns all {@link org.nightlabs.jfire.store.id.ProductID}s that match
//	 *		one of the given {@link org.nightlabs.jfire.store.id.ProductTypeID}s
//	 *		exactly (no inheritance).
//	 */
//	Collection getProductIDs(Set productTypeIDs);

	/**
	 * Hint: Delegate to {@link TransferWizardUtil#getProductTypeByIDMap(Collection)}.
	 *
	 * @param articleContainers A <tt>Collection</tt> of {@link ArticleContainer}.
	 * @return A <tt>Map</tt> of {@link org.nightlabs.jfire.store.id.ProductTypeID} as key
	 *		and {@link org.nightlabs.jfire.store.ProductType} as value.
	 */
	Map getProductTypeByIDMap();

	/**
	 * @return Returns instances of {@link org.nightlabs.jfire.accounting.id.CustomerGroupID}
	 */
	Collection<CustomerGroupID> getCustomerGroupIDs();

	/**
	 * @return Returns instances of {@link DeliveryEntryPage}.
	 */
	List<DeliveryEntryPage> getDeliveryEntryPages();

//	Delivery createDelivery();
//
//	/**
//	 * @return Returns instances of {@link Delivery} which have been previously created
//	 *		by {@link #createDelivery()}.
//	 */
//	List getDeliveries();
//
//	/**
//	 * This method adds a newly created {@link DeliveryData} and stores it to a previously created
//	 * {@link Delivery} object.
//	 *
//	 * @see #createDelivery()
//	 */
//	void addDeliveryData(DeliveryData deliveryData);
//
//	DeliveryData getDeliveryData(Delivery delivery);

//	/**
//	 * @return Returns instances of {@link org.nightlabs.jfire.store.id.DeliveryNoteID}.
//	 */
//	Collection getDeliveryNoteIDs();

	boolean isDeliveryEnabled();
}
