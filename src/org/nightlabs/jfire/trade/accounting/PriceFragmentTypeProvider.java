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

package org.nightlabs.jfire.trade.accounting;

import java.util.Collection;
import java.util.Set;

import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.id.PriceFragmentTypeID;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class PriceFragmentTypeProvider extends JDOObjectProvider {

	/**
	 * 
	 */
	public PriceFragmentTypeProvider() {
		super();
	}
	
	// TODO: Make this a real JDOObjectProvider
	
	/**
	 * @see org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider#retrieveJDOObject(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	protected Object retrieveJDOObject(String scope, Object objectID, String[] fetchGroups, int maxFetchDepth) throws Exception {
		if (!(objectID instanceof PriceFragmentTypeID))
			throw new IllegalArgumentException("PriceFragmentTypeProvider requires a PriceFragmentTypeID as objectID parameter to retrieve a PriceFragmentType."); //$NON-NLS-1$
		return AccountingUtil.getAccountingManager().getPriceFragmentType((PriceFragmentTypeID)objectID, fetchGroups, maxFetchDepth);
	}

	/**
	 * @see org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider#retrieveJDOObjects(java.lang.String, java.lang.Set, java.lang.String[])
	 */
	protected Collection retrieveJDOObjects(String scope, Set objectIDs, String[] fetchGroups, int maxFetchDepth) throws Exception {
		return AccountingUtil.getAccountingManager().getPriceFragmentTypes(objectIDs, fetchGroups, maxFetchDepth);
	}
	
	public Collection getPriceFragmentTypes(String[] fetchGroups, int maxFetchDepth) {
		try {
			return getJDOObjects(null, AccountingUtil.getAccountingManager().getPriceFragmentTypeIDs(), fetchGroups, maxFetchDepth);
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw new RuntimeException(e);
		}
	}
	
	public PriceFragmentType getPriceFragmentType(PriceFragmentTypeID priceFragmentTypeID, String[] fetchGroups, int maxFetchDepth) {
		return (PriceFragmentType)getJDOObject(null, priceFragmentTypeID, fetchGroups, maxFetchDepth);
	}
	
	private static PriceFragmentTypeProvider sharedInstance;
	
	public static PriceFragmentTypeProvider sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new PriceFragmentTypeProvider();
		return sharedInstance;
	}
	

}
