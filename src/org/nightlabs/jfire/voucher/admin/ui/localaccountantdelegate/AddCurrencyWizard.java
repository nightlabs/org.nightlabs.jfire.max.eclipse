package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CurrencySelectionPage;

public class AddCurrencyWizard
		extends DynamicPathWizard
{
	private VoucherLocalAccountantDelegateComposite voucherLocalAccountantDelegateComposite;
	private CurrencySelectionPage currencySelectionPage;

	public AddCurrencyWizard(VoucherLocalAccountantDelegateComposite voucherLocalAccountantDelegateComposite)
	{
		this.voucherLocalAccountantDelegateComposite = voucherLocalAccountantDelegateComposite;
	}

	@Override
	public void addPages()
	{
		currencySelectionPage = new CurrencySelectionPage();
		addPage(currencySelectionPage);
	}

	@Override
	@Implement
	public boolean performFinish()
	{
		voucherLocalAccountantDelegateComposite.addCurrency(currencySelectionPage.getSelectedCurrency());
		return true;
	}

}
