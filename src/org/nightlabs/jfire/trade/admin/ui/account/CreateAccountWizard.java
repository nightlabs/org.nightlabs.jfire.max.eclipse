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

package org.nightlabs.jfire.trade.admin.ui.account;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.account.editor.AbstractAccountPageController;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class CreateAccountWizard 
extends DynamicPathWizard 
{	
	public CreateAccountWizard() {
		super();
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountWizard.windowTitle")); //$NON-NLS-1$
	}
	
	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	public IDynamicPathWizardPage createWizardEntryPage() {
		return new CreateAccountEntryWizardPage();
	}
	
	protected CreateAccountEntryWizardPage getCreateAccountEntryPage() {
		return (CreateAccountEntryWizardPage)getWizardEntryPage();
	}
	
	private CreateAccountEntryWizardPage getEntryPage() {
		return (CreateAccountEntryWizardPage)getWizardEntryPage();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		Account newAccount = null;
		try {
			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			OrganisationLegalEntity owner = tm.getOrganisationLegalEntity(Login.getLogin().getOrganisationID(), true, new String[] { FetchPlan.DEFAULT }, 1); // TODO make this nicer - e.g. by using a DAO and maybe restructuring this whole wizard

			if (getCreateAccountEntryPage().isCreateSummaryAccount())
				newAccount = new SummaryAccount(
						Login.getLogin().getOrganisationID(),
						getEntryPage().getAnchorID(),
						owner,
						getEntryPage().getCurrency(),
						false);
			else
				newAccount = new Account(
						Login.getLogin().getOrganisationID(),
						Account.ANCHOR_TYPE_ID_LOCAL_REVENUE, // TODO Account: This type should be selectable in the wizard!
						getEntryPage().getAnchorID(),
						owner,
						getEntryPage().getCurrency(),
						false);

//			newAccount = accountingManager.createMandatorAccount(
//				getEntryPage().getAnchorID(), 
//				getEntryPage().getCurrency().getCurrencyID(),
//				getCreateAccountEntryPage().isCreateSummaryAccount(),
//				true,
//				AccountTable.ACCOUNT_DEFAULT_FETCHGROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
//			);
			((I18nTextBuffer)getCreateAccountEntryPage().getAccountNameEditor().getI18nText()).copyTo(newAccount.getName());
			newAccount = AccountDAO.sharedInstance().storeAccount(newAccount, true, 
//					AccountTable.ACCOUNT_DEFAULT_FETCHGROUP,
					AbstractAccountPageController.FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			return false;
		}
		return true;
	}

}
