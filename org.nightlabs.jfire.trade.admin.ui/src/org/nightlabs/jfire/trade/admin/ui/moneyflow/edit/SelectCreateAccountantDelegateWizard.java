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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.edit;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateTree;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.accounting.AccountingUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SelectCreateAccountantDelegateWizard extends DynamicPathWizard {

	private SelectAccountantDelegateWizardPage selectDelegatePage;
	private CreateAccountantDelegateWizardPage createDelegatePage;
	
	private LocalAccountantDelegate selectedDelegate;
	
	public SelectCreateAccountantDelegateWizard() {
		super();
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectCreateAccountantDelegateWizard.windowTitle")); //$NON-NLS-1$
		selectDelegatePage = new SelectAccountantDelegateWizardPage(this);
		createDelegatePage = new CreateAccountantDelegateWizardPage();
		addPage(selectDelegatePage);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			if (selectDelegatePage.isCreateNew()) {
				selectedDelegate = selectDelegatePage.getSelectedDelegateType().createNewDelegate(
						selectDelegatePage.getSelectedDelegate(),
						Login.getLogin().getOrganisationID(),
						createDelegatePage.getDelegateID()
				);
				if (selectedDelegate == null)
					throw new IllegalStateException("LocalAccountantDelegateType did return null in createNewDelegate. Selected type was "+selectDelegatePage.getSelectedDelegateType().getDescription()); //$NON-NLS-1$
				selectedDelegate.getName().copyFrom(createDelegatePage.getDelegateName());
				selectedDelegate = AccountingUtil.getAccountingManager().storeLocalAccountantDelegate(
						selectedDelegate,
						true,
						LocalAccountantDelegateTree.DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
					);
			}
			else {
				selectedDelegate = selectDelegatePage.getSelectedDelegate();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/**
	 * @return Returns the createDelegatePage.
	 */
	public CreateAccountantDelegateWizardPage getCreateDelegatePage() {
		return createDelegatePage;
	}

	/**
	 * @return Returns the selectDelegatePage.
	 */
	public SelectAccountantDelegateWizardPage getSelectDelegatePage() {
		return selectDelegatePage;
	}
	
	public LocalAccountantDelegate getSelectedDelegate() {
		return selectedDelegate;
	}

	public boolean isLocalAccountantDelegateInherited()
	{
		return false; // TODO we should have UI for this!
	}
}
