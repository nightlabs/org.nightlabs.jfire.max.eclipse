package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;

public class SelectAccountWizard
		extends DynamicPathWizard
{
	private SelectAccountWizardPage selectAccountWizardPage;

	private Currency currency;
	private Account preselectedAccount;

	/**
	 * @param currency The currency for which to select a voucher-account. Must not be <code>null</code>!
	 * @param selectedAccount The previously selected account or <code>null</code>, if there is none. If assigned, the
	 *		{@link SelectAccountWizardPage} will pre-select this account.
	 */
	public SelectAccountWizard(Currency currency, Account preselectedAccount)
	{
		this.currency = currency;
		this.preselectedAccount = preselectedAccount;
	}

	@Override
	public void addPages()
	{
		selectAccountWizardPage = new SelectAccountWizardPage(currency, preselectedAccount);
		addPage(selectAccountWizardPage);
	}

	@Implement
	public boolean performFinish()
	{
		switch (selectAccountWizardPage.getMode()) {
			case CREATE:
				selectedAccount = selectAccountWizardPage.createAccount(); // this does not yet store it to the server - ALL is stored when the main wizard stores its data
				break;
			case SELECT:
				selectedAccount = selectAccountWizardPage.getSelectedAccount();
				break;
			default:
				throw new IllegalStateException("Unknown mode: " + selectAccountWizardPage.getMode()); //$NON-NLS-1$
		}

		return true;
	}

	private Account selectedAccount = null;

	public Account getSelectedAccount()
	{
		return selectedAccount;
	}
}
