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

/**
 * This implementation of
 * {@link org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory}
 * represents a manual delivery (hand-to-hand-delivery).
 * Hence, it is a dummy, which doesn't perform any action.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientDeliveryProcessorFactoryManual extends
		AbstractClientDeliveryProcessorFactory
{

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#init()
	 */
	public void init()
	{
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory#createClientDeliveryProcessor()
	 */
	public ClientDeliveryProcessor createClientDeliveryProcessor()
	{
		return new ClientDeliveryProcessorManual();
	}

}
