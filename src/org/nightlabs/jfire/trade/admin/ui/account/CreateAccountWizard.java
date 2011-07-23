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

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
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
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.account.editor.AccountEditor;
import org.nightlabs.jfire.trade.ui.account.editor.AccountEditorInput;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class CreateAccountWizard
extends DynamicPathWizard
implements INewWizard
{
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		AccountType.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_ACCOUNT_TYPE,
		Account.FETCH_GROUP_CURRENCY,
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_OWNER,
//		Account.FETCH_GROUP_SUMMARY_ACCOUNTS,
		Account.FETCH_GROUP_DESCRIPTION,
//		SummaryAccount.FETCH_GROUP_SUMMED_ACCOUNTS
	};	
	
	private CreateAccountEntryWizardPage createAccountEntryWizardPage;

	public CreateAccountWizard() {
		super();
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountWizard.windowTitle")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	@Override
	public IDynamicPathWizardPage createWizardEntryPage() {
		return new CreateAccountEntryWizardPage();
	}

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
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress(){
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask("Save Account", 200);
						Account newAccount = null;
						OrganisationLegalEntity owner = LegalEntityDAO.sharedInstance().getOrganisationLegalEntity(
								Login.getLogin().getOrganisationID(), true, new String[] { FetchPlan.DEFAULT }, 1,
								new SubProgressMonitor(new ProgressMonitorWrapper(monitor), 50));

						if (getCreateAccountEntryWizardPage().isCreateSummaryAccount()) {
							AccountType accountType = AccountTypeDAO.sharedInstance().getAccountType(
									AccountType.ACCOUNT_TYPE_ID_SUMMARY,
									new String[] { FetchPlan.DEFAULT },
									1,
									new SubProgressMonitor(new ProgressMonitorWrapper(monitor), 50)
							);

							newAccount = new SummaryAccount(
									Login.getLogin().getOrganisationID(),
									getCreateAccountEntryWizardPage().getAnchorID(),
									accountType,
									owner,
									getCreateAccountEntryWizardPage().getCurrency());
						}
						else {
							newAccount = new Account(
									Login.getLogin().getOrganisationID(),
									getCreateAccountEntryWizardPage().getAnchorID(),
									getCreateAccountEntryWizardPage().getAccountType(),
									owner,
									getCreateAccountEntryWizardPage().getCurrency());
						}
						((I18nTextBuffer)getCreateAccountEntryWizardPage().getAccountNameEditor().getI18nText()).copyTo(newAccount.getName());
						newAccount = AccountDAO.sharedInstance().storeAccount(newAccount, 
								true,
								FETCH_GROUPS,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(new ProgressMonitorWrapper(monitor), 150));
						monitor.done();

						Editor2PerspectiveRegistry.sharedInstance().openEditor(
								new AccountEditorInput((AnchorID) JDOHelper.getObjectId(newAccount)), AccountEditor.EDITOR_ID);
					} catch (Exception e) {
						ExceptionHandlerRegistry.asyncHandleException(e);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public static boolean open() {
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(
				new CreateAccountWizard()
			);
		return dlg.open() == Window.OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// do nothing
	}

}
