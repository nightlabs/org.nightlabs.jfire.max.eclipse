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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryData;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DeliveryWizardHop extends WizardHop
{
	private List<Delivery> deliveryList;
	
	private Map<Delivery, DeliveryData> deliveryDataMap;
	
	private boolean single;
	
	private DeliveryWizardHop(DeliveryEntryPage entryPage) {
		super(entryPage);
		
		this.deliveryList = new LinkedList<Delivery>();
		this.deliveryDataMap = new HashMap<Delivery, DeliveryData>();
	}

	/**
	 * @param entryPage
	 */
	public DeliveryWizardHop(DeliveryEntryPage entryPage, Delivery delivery)
	{
		this(entryPage);

		if (delivery == null)
			throw new IllegalArgumentException("delivery must not be null"); //$NON-NLS-1$
		
		this.deliveryList.add(delivery);
		this.single = true;
	}
	
	public DeliveryWizardHop(DeliveryEntryPage entryPage, List<Delivery> deliveryList)
	{
		this(entryPage);

		if (deliveryList.isEmpty())
			throw new IllegalArgumentException("deliveryList must be non-empty."); //$NON-NLS-1$
		
		this.deliveryList.addAll(deliveryList);
		
		if (deliveryList.size() > 1)
			this.single = false;
	}

	public DeliveryEntryPage getDeliveryEntryPage()
	{
		return (DeliveryEntryPage)getEntryPage();
	}

	public Delivery getDelivery()
	{
		if (!this.single)
			throw new IllegalStateException("This DeliveryWizardHop has been created with multiple deliveries. Calling this method is thus not possible."); //$NON-NLS-1$
		
		return deliveryList.get(0);
	}
	
	public List<Delivery> getDeliveryList() {
		return this.deliveryList;
	}

	public void setDeliveryData(DeliveryData deliveryData)
	{
		if (!this.single)
			throw new IllegalStateException("This DeliveryWizardHop has been created with multiple deliveries. Calling this method is thus not possible."); //$NON-NLS-1$
		
		this.deliveryDataMap.put(getDelivery(), deliveryData);
	}
	
	/**
	 * @return Returns the deliveryData.
	 */
	public DeliveryData getDeliveryData()
	{
		if (!this.single)
			throw new IllegalStateException("This DeliveryWizardHop has been created with multiple deliveries. Calling this method is thus not possible."); //$NON-NLS-1$
		
		return getDeliveryData(getDelivery());
	}
	
	public DeliveryData getDeliveryData(Delivery delivery) {
		return deliveryDataMap.get(delivery);
	}
}
