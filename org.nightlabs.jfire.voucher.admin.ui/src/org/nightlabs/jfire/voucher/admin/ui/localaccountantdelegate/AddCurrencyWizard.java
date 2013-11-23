package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CurrencySelectionPage;

public class AddCurrencyWizard extends org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.AddCurrencyWizard
{
	private final VoucherLocalAccountantDelegateComposite voucherLocalAccountantDelegateComposite;

	public AddCurrencyWizard(final VoucherLocalAccountantDelegateComposite voucherLocalAccountantDelegateComposite)
	{
		this.voucherLocalAccountantDelegateComposite = voucherLocalAccountantDelegateComposite;
	}

	@Override
	public void addPages()
	{
		CurrencySelectionPage currencySelectionPage = new CurrencySelectionPage();
		setCurrencySelectionPage(currencySelectionPage);
		addPage(currencySelectionPage);
	}

	@Override
	public boolean performFinish()
	{
		super.performFinish();
		voucherLocalAccountantDelegateComposite.addCurrency(getCurrency());
		return true;
	}
}
