package org.nightlabs.jfire.voucher.admin.ui.priceconfig;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CurrencySelectionPage;

public class AddCurrencyWizard
		extends DynamicPathWizard
{
	private CurrencyAmountTable currencyAmountTable;
	private CurrencySelectionPage currencySelectionPage;

	public AddCurrencyWizard(CurrencyAmountTable currencyAmountTable)
	{
		this.currencyAmountTable = currencyAmountTable;
	}

	@Override
	public void addPages()
	{
		currencySelectionPage = new CurrencySelectionPage();
		addPage(currencySelectionPage);
	}

	@Override
	public boolean performFinish()
	{
		currencyAmountTable.addCurrency(currencySelectionPage.getSelectedCurrency());
		return true;
	}
}
