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

import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.DeliveryException;
import org.nightlabs.jfire.store.deliver.DeliveryResult;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientDeliveryProcessorManual
extends AbstractClientDeliveryProcessor
{
	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public DeliveryData getDeliveryData()
	{
		return null;
	}

	@Override
	public IWizardHopPage createDeliveryWizardPage()
	{
		return null;
	}

	@Override
	public DeliveryResult deliverBegin() throws DeliveryException
	{
		return null;
	}

	@Override
	public DeliveryResult deliverDoWork() throws DeliveryException
	{
		return null;
	}

	@Override
	public DeliveryResult deliverEnd() throws DeliveryException
	{
		return null;
	}

}
