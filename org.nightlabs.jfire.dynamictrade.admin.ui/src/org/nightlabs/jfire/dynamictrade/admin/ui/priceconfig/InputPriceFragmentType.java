package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;

public class InputPriceFragmentType
{
	private DimensionValue.PriceFragmentTypeDimensionValue priceFragmentTypeDimensionValue;

	private boolean input;

	public InputPriceFragmentType(DimensionValue.PriceFragmentTypeDimensionValue priceFragmentTypeDimensionValue)
	{
		this.priceFragmentTypeDimensionValue = priceFragmentTypeDimensionValue;
	}

	public DimensionValue.PriceFragmentTypeDimensionValue getPriceFragmentTypeDimensionValue()
	{
		return priceFragmentTypeDimensionValue;
	}

	public PriceFragmentType getPriceFragmentType()
	{
		return (PriceFragmentType) priceFragmentTypeDimensionValue.getObject();
	}

	public boolean isInput()
	{
		return input;
	}
	public void setInput(boolean input)
	{
		this.input = input;
	}
}
