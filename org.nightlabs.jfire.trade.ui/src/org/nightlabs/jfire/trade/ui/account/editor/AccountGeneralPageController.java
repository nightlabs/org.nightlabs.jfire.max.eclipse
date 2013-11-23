/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.jfire.trade.ui.account.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.trade.LegalEntity;

/**
 * @author Daniel Mazurek <!-- daniel [AT] nightlabs [DOT] de -->
 */
public class AccountGeneralPageController
extends AbstractAccountPageController
{
//	private static final Logger logger = Logger.getLogger(AccountGeneralPageController.class);
	
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		AccountType.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_ACCOUNT_TYPE,
		Account.FETCH_GROUP_CURRENCY,
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_OWNER,
		Account.FETCH_GROUP_DESCRIPTION,
		LegalEntity.FETCH_GROUP_PERSON,
	};	
	
	/**
	 * Create an instance of this controller for
	 * an {@link UserEditor} and load the data.
	 */
	public AccountGeneralPageController(EntityEditor editor) {
		super(editor);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}
	
//	/**
//	 * Save the user data.
//	 * @param monitor The progress monitor to use.
//	 */
//	@Override
//	public boolean doSave(ProgressMonitor monitor)
//	{
//		for (IFormPage page : getPages()) {
//			if (page instanceof AccountGeneralPage) {
//				final AccountGeneralPage gp = (AccountGeneralPage) page;
//				Account account = gp.getAccountGeneralSection().getAccountEditorComposite().getAccount();
//				this.account = AccountDAO.sharedInstance().storeAccount(account,
//						true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						monitor);
////				doLoad(new org.nightlabs.progress.NullProgressMonitor());
//				return true;
//			}
//		}
//		return false;
//	}
}
