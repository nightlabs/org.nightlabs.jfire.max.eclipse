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

import java.util.Collection;
import java.util.Set;

import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider;
import org.nightlabs.jfire.trade.ui.accounting.AccountingUtil;

/**
 * Provider for LocalAccountantDelegates.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class LocalAccountantDelegateProvider extends JDOObjectProvider {

	/**
	 * 
	 */
	public LocalAccountantDelegateProvider() {
		super();
	}

	/**
	 * @see org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider#retrieveJDOObject(java.lang.String, java.lang.Object, java.lang.String[])
	 */
	@Override
	protected Object retrieveJDOObject(String scope, Object objectID, String[] fetchGroups, int maxFetchDepth) throws Exception {
		if (!(objectID instanceof LocalAccountantDelegateID))
			throw new IllegalArgumentException("LocalAccountantDelegateProvider can only handle LocalAccountantDelegateID but received "+objectID.getClass().getName()); //$NON-NLS-1$
		return AccountingUtil.getAccountingManager().getLocalAccountantDelegate((LocalAccountantDelegateID)objectID, fetchGroups, maxFetchDepth);
	}

	/**
	 * @see org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider#retrieveJDOObjects(java.lang.String, java.util.Set, java.lang.String[])
	 */
	@Override
	protected Collection retrieveJDOObjects(String scope, Set objectIDs, String[] fetchGroups, int maxFetchDepth) throws Exception {
		return AccountingUtil.getAccountingManager().getLocalAccountantDelegates(objectIDs, fetchGroups, maxFetchDepth);
	}
	
	/**
	 * Get the LocalAccountantDelegate for the given delegateID.
	 * 
	 * @param delegateID The delegateID of the desired LocalAccountantDelegate.
	 * @param fetchGroups The fetchGroups to detach the delegate with.
	 * @return A cached version of the LocalAccountantDelegate with the given id.
	 */
	public LocalAccountantDelegate getDelegate(LocalAccountantDelegateID delegateID, String[] fetchGroups, int maxFetchDepth) {
		return (LocalAccountantDelegate) getJDOObject(null, delegateID, fetchGroups, maxFetchDepth);
	}
	
	public Collection getTopLevelDelegates(Class delegateClass, String[] fetchGroups, int maxFetchDepth) {
		try {
			return getJDOObjects(
					null,
					AccountingUtil.getAccountingManager().getTopLevelAccountantDelegates(delegateClass),
					fetchGroups, maxFetchDepth
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get all LocalAccountantDelegates that have the delegate with the given
	 * id as extendedLocalAccountantDelegate.
	 * 
	 * @param delegateID The parent delegate id.
	 * @param fetchGroups The fetchGroups to detach the children with.
	 */
	public Collection getChildDelegates(LocalAccountantDelegateID delegateID, String[] fetchGroups, int maxFetchDepth) {
		try {
			return getJDOObjects(
					null,
					AccountingUtil.getAccountingManager().getChildAccountantDelegates(delegateID),
					fetchGroups, maxFetchDepth
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static LocalAccountantDelegateProvider sharedInstance;
	
	public static LocalAccountantDelegateProvider sharedInstance(){
		if (sharedInstance == null)
			sharedInstance = new LocalAccountantDelegateProvider();
		return sharedInstance;
	}
}
