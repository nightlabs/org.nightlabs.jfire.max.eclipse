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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractClientDeliveryProcessorFactory
implements ClientDeliveryProcessorFactory
{
	private String id;
	private String clientDeliveryProcessorFactoryName;

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#setID(java.lang.String)
	 */
	public void setID(String id)
	{
		this.id = id;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#getID()
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#setName(java.lang.String)
	 */
	public void setName(
			String clientDeliveryProcessorFactoryName)
	{
		this.clientDeliveryProcessorFactoryName = clientDeliveryProcessorFactoryName;
	}

	/**
	 * @return Returns the clientDeliveryProcessorFactoryName.
	 */
	public String getName()
	{
		return clientDeliveryProcessorFactoryName;
	}


	private Set<ModeOfDeliveryRef> modeOfDeliveryRefs = new HashSet<ModeOfDeliveryRef>();

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#addModeOfDeliveryRef(org.nightlabs.jfire.trade.ui.deliver.ClientDeliveryProcessorFactory.ModeOfDeliveryRef)
	 */
	public void addModeOfDeliveryRef(ModeOfDeliveryRef modeOfDeliveryRef)
	{
		modeOfDeliveryRefs.add(modeOfDeliveryRef);
		unmodifiableModeOfDeliveryRefs = null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#removeModeOfDeliveryRef(org.nightlabs.jfire.trade.ui.deliver.ClientDeliveryProcessorFactory.ModeOfDeliveryRef)
	 */
	public void removeModeOfDeliveryRef(ModeOfDeliveryRef modeOfDeliveryRef)
	{
		modeOfDeliveryRefs.remove(modeOfDeliveryRef);
		unmodifiableModeOfDeliveryRefs = null;
	}

	private Collection<ModeOfDeliveryRef> unmodifiableModeOfDeliveryRefs = null;
	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#getModeOfDeliveryRefs()
	 */
	public Collection<ModeOfDeliveryRef> getModeOfDeliveryRefs()
	{
		if (unmodifiableModeOfDeliveryRefs == null)
			unmodifiableModeOfDeliveryRefs = Collections.unmodifiableCollection(modeOfDeliveryRefs);

		return unmodifiableModeOfDeliveryRefs;
	}


	private Set<ModeOfDeliveryFlavourRef> modeOfDeliveryFlavourRefs = new HashSet<ModeOfDeliveryFlavourRef>();

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#addModeOfDeliveryFlavourRef(org.nightlabs.jfire.trade.ui.deliver.ClientDeliveryProcessorFactory.ModeOfDeliveryFlavourRef)
	 */
	public void addModeOfDeliveryFlavourRef(
			ModeOfDeliveryFlavourRef modeOfDeliveryFlavourRef)
	{
		modeOfDeliveryFlavourRefs.add(modeOfDeliveryFlavourRef);
		unmodifiableModeOfDeliveryFlavourRefs = null;
	}
	
	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#removeModeOfDeliveryFlavourRef(org.nightlabs.jfire.trade.ui.deliver.ClientDeliveryProcessorFactory.ModeOfDeliveryFlavourRef)
	 */
	public void removeModeOfDeliveryFlavourRef(
			ModeOfDeliveryFlavourRef modeOfDeliveryFlavourRef)
	{
		modeOfDeliveryFlavourRefs.remove(modeOfDeliveryFlavourRef);
		unmodifiableModeOfDeliveryFlavourRefs = null;
	}

	private Collection<ModeOfDeliveryFlavourRef> unmodifiableModeOfDeliveryFlavourRefs = null;

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#getModeOfDeliveryFlavourRefs()
	 */
	public Collection<ModeOfDeliveryFlavourRef> getModeOfDeliveryFlavourRefs()
	{
		if (unmodifiableModeOfDeliveryFlavourRefs == null)
			unmodifiableModeOfDeliveryFlavourRefs = Collections.unmodifiableCollection(modeOfDeliveryFlavourRefs);

		return unmodifiableModeOfDeliveryFlavourRefs;
	}

}
