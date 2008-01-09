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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.accounting.dao.AccountTypeDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.account.editor.AbstractAccountPageController;
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

	private CreateAccountEntryWizardPage createAccountEntryWizardPage;

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	@Override
	public IDynamicPathWizardPage createWizardEntryPage() {
		return new CreateAccountEntryWizardPage();
	}
	
//	protected CreateAccountEntryWizardPage getCreateAccountEntryPage() {
//		return (CreateAccountEntryWizardPage)getWizardEntryPage();
//	}
//	
//	private CreateAccountEntryWizardPage getEntryPage() {
//		return (CreateAccountEntryWizardPage)getWizardEntryPage();
//	}

	@Override
	public void addPages()
	{
		createAccountEntryWizardPage = new CreateAccountEntryWizardPage();
		addPage(createAccountEntryWizardPage);
	}

	protected CreateAccountEntryWizardPage getCreateAccountEntryWizardPage()
	{
		return createAccountEntryWizardPage;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Account newAccount = null;
		// TODO async => Job!
		try {
			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			OrganisationLegalEntity owner = tm.getOrganisationLegalEntity(Login.getLogin().getOrganisationID(), true, new String[] { FetchPlan.DEFAULT }, 1); // TODO make this nicer - e.g. by using a DAO and maybe restructuring this whole wizard

			if (getCreateAccountEntryWizardPage().isCreateSummaryAccount()) {
				AccountType accountType = AccountTypeDAO.sharedInstance().getAccountType(
						AccountType.ACCOUNT_TYPE_ID_SUMMARY,
						new String[] { FetchPlan.DEFAULT },
						1, new NullProgressMonitor()
				);
				
				newAccount = new SummaryAccount(
						Login.getLogin().getOrganisationID(),
						getCreateAccountEntryWizardPage().getAnchorID(),
						accountType,
						owner,
						getCreateAccountEntryWizardPage().getCurrency());
			}
			else
				newAccount = new Account(
						Login.getLogin().getOrganisationID(),
						getCreateAccountEntryWizardPage().getAnchorID(),
						getCreateAccountEntryWizardPage().getAccountType(),
						owner,
						getCreateAccountEntryWizardPage().getCurrency());

			((I18nTextBuffer)getCreateAccountEntryWizardPage().getAccountNameEditor().getI18nText()).copyTo(newAccount.getName());
			AccountDAO.sharedInstance().storeAccount(newAccount, false, 
					AbstractAccountPageController.FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			return false;
		}
		return true;
	}

	public static boolean open() {
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(
				Display.getDefault().getActiveShell(),
				new CreateAccountWizard()
			);
		return dlg.open() == Dialog.OK;
	}
	
}
