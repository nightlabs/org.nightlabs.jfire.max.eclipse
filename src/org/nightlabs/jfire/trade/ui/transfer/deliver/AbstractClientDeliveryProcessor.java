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

import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.transfer.wizard.IDeliveryEntryPage;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractClientDeliveryProcessor
implements ClientDeliveryProcessor
{
	// the following fields are set BEFORE the init() method is called.
	private ClientDeliveryProcessorFactory clientDeliveryProcessorFactory;
	private AnchorID customerID;

	private Delivery delivery;

	// the following fields are set AFTER the init() method is called.
	private DeliveryNoteID deliveryNoteID = null;

	private IDeliveryEntryPage deliveryEntryPage;

	public IDeliveryEntryPage getDeliveryEntryPage()
	{
		return deliveryEntryPage;
	}
	public void setDeliveryEntryPage(IDeliveryEntryPage deliveryEntryPage)
	{
		this.deliveryEntryPage = deliveryEntryPage;
	}

	public void init()
	{
		// nothing to do - but this might change
	}

	public String getRequirementCheckKey()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor#setClientDeliveryProcessorFactory(org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory)
	 */
	public void setClientDeliveryProcessorFactory(
			ClientDeliveryProcessorFactory clientDeliveryProcessorFactory)
	{
		this.clientDeliveryProcessorFactory = clientDeliveryProcessorFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor#setCustomerID(org.nightlabs.jfire.transfer.id.AnchorID)
	 */
	public void setCustomerID(AnchorID customerID)
	{
		this.customerID = customerID;
	}

	/**
	 * @return Returns the clientDeliveryProcessorFactory.
	 */
	public ClientDeliveryProcessorFactory getClientDeliveryProcessorFactory()
	{
		return clientDeliveryProcessorFactory;
	}
	/**
	 * @return Returns the customerID.
	 */
	public AnchorID getCustomerID()
	{
		return customerID;
	}

	/**
	 * @return Returns the delivery.
	 */
	public Delivery getDelivery()
	{
		return delivery;
	}
	/**
	 * @param delivery The delivery to set.
	 */
	public void setDelivery(Delivery delivery)
	{
		this.delivery = delivery;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor#getDeliveryNoteID()
	 */
	public DeliveryNoteID getDeliveryNoteID()
	{
		return deliveryNoteID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor#setDeliveryNoteID(org.nightlabs.jfire.store.id.DeliveryNoteID)
	 */
	public void setDeliveryNoteID(DeliveryNoteID deliveryNoteID)
	{
		this.deliveryNoteID = deliveryNoteID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor#getIncludedServerDeliveryProcessorIDs()
	 */
	public Set getIncludedServerDeliveryProcessorIDs()
	{
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor#getExcludedServerDeliveryProcessorIDs()
	 */
	public Set getExcludedServerDeliveryProcessorIDs()
	{
		return null;
	}

}
