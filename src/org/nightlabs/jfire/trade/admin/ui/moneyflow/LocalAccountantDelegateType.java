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

import java.util.List;
import java.util.Map;

import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.store.ProductType;

/**
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface LocalAccountantDelegateType {

	/**
	 * Returns the class of the LocalAccountantDelegate.
	 */
	public Class getDelegateClass();
	
	/**
	 * Retuns a list of MoneyFlowDimensionIDs this type knows.
	 */
	public List getMoneyFlowDimensionIDs();

	/**
	 * Returns a short name of this type. This is for use in Combos etc.
	 */
	public String getName();

	/**
	 * Returns a descriptive text pointing out what this type of delegate
	 * does. E.g. which dimensions it knows. 
	 */
	public String getDescription();

	/**
	 * Create a new LocalAccountantDelegate of this type.
	 *
	 * @param extendedDelegate The delegate the new delegate should extend.
	 * @param organisationID The organisationID of the new delgate.
	 * @param localAccountantDelegateID The localAccountantDelegateID of the new delegate.
	 * @return A new LocalAccountantDelegate.
	 */
	public LocalAccountantDelegate createNewDelegate(LocalAccountantDelegate extendedDelegate, String organisationID, String localAccountantDelegateID);
	
	
	public MoneyFlowMapping createNewMapping(
			ProductType productType,
			String packageType, 
			Map dimensionValues,			
			Currency currency,
			Account revenueAccount,
			Account expenseAccount, Account reverseRevenueAccount, Account reverseExpenseAccount
		);
	
	public boolean canHandleMappingType(Class mappingType);
	
	public String getMappingDescription(MoneyFlowMapping mapping);
	
}
