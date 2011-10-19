package org.nightlabs.jfire.voucher.admin.ui.priceconfig;

import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CurrencySelectionPage;

public class AddCurrencyWizard extends org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.AddCurrencyWizard
{
	private final CurrencyAmountTable currencyAmountTable;

	public AddCurrencyWizard(final CurrencyAmountTable currencyAmountTable)
	{
		this.currencyAmountTable = currencyAmountTable;
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
		currencyAmountTable.addCurrency(getCurrency());
		return true;
	}
}
