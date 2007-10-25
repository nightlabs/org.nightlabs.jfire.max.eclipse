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

package org.nightlabs.jfire.trade.ui.accounting;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.dnd.Transfer;

import org.nightlabs.base.ui.dnd.LocalObjectTransfer;
import org.nightlabs.jfire.accounting.Account;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AccountLocalTransfer 
extends LocalObjectTransfer 
{
	public AccountLocalTransfer() {
		super();
	}
	
	/**
	 * @see org.nightlabs.base.ui.dnd.LocalObjectTransfer#validate(java.lang.Object)
	 */
	@Override
	protected boolean validate(Object object) {
		if (object instanceof Account)
			return true;
		else if (object instanceof Collection) {
			for (Iterator iter = ((Collection)object).iterator(); iter.hasNext();) {
				if (! (iter.next() instanceof Account))
					return false;				
			}
			return true;
		}
		return false;
	}
	
	private static AccountLocalTransfer sharedAccountLocalTransfer;
	
	public static AccountLocalTransfer sharedAccountLocalTransfer() {
		if (sharedAccountLocalTransfer == null)
			sharedAccountLocalTransfer = new AccountLocalTransfer();
		return sharedAccountLocalTransfer;
	}
	
	public static final Transfer[] ACCOUNT_LOCAL_TRANSFERS = new Transfer[] { sharedAccountLocalTransfer()};
}
