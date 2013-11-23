package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.nightlabs.jfire.accounting.PriceFragmentType;

public class InputPriceFragmentType
{
	public InputPriceFragmentType(PriceFragmentType priceFragmentType)
	{
		this.priceFragmentType = priceFragmentType;
	}

	private PriceFragmentType priceFragmentType;
//	private Currency currency;
	private long amount;

	public long getAmount()
	{
		return amount;
	}
	public void setAmount(long amount)
	{
		this.amount = amount;
	}
	public PriceFragmentType getPriceFragmentType()
	{
		return priceFragmentType;
	}
}
