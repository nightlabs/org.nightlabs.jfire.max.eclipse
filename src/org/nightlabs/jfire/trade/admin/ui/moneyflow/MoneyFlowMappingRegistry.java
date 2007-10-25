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

package org.nightlabs.jfire.trade.admin.ui.moneyflow;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @deprecated Not necessary anymore - will be removed soon.
 */
@Deprecated
public class MoneyFlowMappingRegistry
// implements Registry
{

	/**
	 * 
	 */
	public MoneyFlowMappingRegistry() {
		super();
	}

//	public String getOrganisationID() {
//		try {
//			return Login.getLogin().getOrganisationID();
//		} catch (LoginException e) {
//			throw new RuntimeException(e);
//		}
//	}
//	
//	public int createMoneyFlowMappingID() {
//		try {
//			return AccountingUtil.getAccountingManager().createMoneyFlowMappingID();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	private static MoneyFlowMappingRegistry sharedInstance;

	public static MoneyFlowMappingRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new MoneyFlowMappingRegistry();
		return sharedInstance;
	}
}
