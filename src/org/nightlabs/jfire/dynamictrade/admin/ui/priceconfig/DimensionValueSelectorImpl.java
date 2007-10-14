package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValueSelectorComboImpl;

public class DimensionValueSelectorImpl
extends DimensionValueSelectorComboImpl
{

	public DimensionValueSelectorImpl(Composite parent)
	{
		super(parent, SWT.NONE);
		getGridData().grabExcessVerticalSpace = true;
	}

	private PriceFragmentTypeTable priceFragmentTypeTable;

	@Override
	protected void createDimensionUIElements(Label[] dimensionLabels, Combo[] dimensionCombos, int dimensionIdx, Dimension dimension)
	{
		if (dimension instanceof Dimension.PriceFragmentTypeDimension) {
			priceFragmentTypeTable = new PriceFragmentTypeTable(this);
			priceFragmentTypeTable.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event)
				{
					fireSelectionChangedEvent();
				}
			});

			createDimensionUIElements_createAddButton(dimensionLabels, dimensionCombos, dimensionIdx, dimension);
		}
		else
			super.createDimensionUIElements(dimensionLabels, dimensionCombos, dimensionIdx, dimension);
	}

	List<InputPriceFragmentType> inputPriceFragmentTypes;

	@Override
	protected void fillDimensionCombo(Dimension dimension, Combo dimensionCombo)
	{
		DynamicTradePriceConfig dynamicTradePriceConfig = (DynamicTradePriceConfig) getGridPriceConfig();

		if (dimension instanceof Dimension.PriceFragmentTypeDimension) {
			List priceFragmentTypes = dimension.getValues();
			inputPriceFragmentTypes = new ArrayList<InputPriceFragmentType>(priceFragmentTypes.size());
			for (Iterator it = priceFragmentTypes.iterator(); it.hasNext();) {
				DimensionValue.PriceFragmentTypeDimensionValue priceFragmentTypeDimensionValue = (DimensionValue.PriceFragmentTypeDimensionValue)it.next();
//				PriceFragmentType priceFragmentType = (PriceFragmentType) it.next();
				InputPriceFragmentType inputPriceFragmentType = new InputPriceFragmentType(priceFragmentTypeDimensionValue);
				inputPriceFragmentType.setInput(dynamicTradePriceConfig.getInputPriceFragmentTypes().contains(priceFragmentTypeDimensionValue.getObject()));
				inputPriceFragmentTypes.add(inputPriceFragmentType);
			}
			priceFragmentTypeTable.setInput(dynamicTradePriceConfig, inputPriceFragmentTypes);
		}
		else
			super.fillDimensionCombo(dimension, dimensionCombo);
	}

	private int priceFragmentTypeDimensionIndex = -1;
	protected int getPriceFragmentTypeDimensionIndex()
	{
		if (priceFragmentTypeDimensionIndex < 0) {
			int index = 0;
			for (Dimension dimension : getDimensions()) {
				if (dimension instanceof Dimension.PriceFragmentTypeDimension) {
					priceFragmentTypeDimensionIndex = index;
					break;
				}
				++index;
			}
		}

		if (priceFragmentTypeDimensionIndex < 0)
			throw new IllegalStateException("No PriceFragmentType Dimension!!!"); //$NON-NLS-1$

		return priceFragmentTypeDimensionIndex;
	}

	@Override
	public DimensionValue getSelectedDimensionValue(int dimensionIdx, boolean throwExceptionIfNothingSelected)
	{
		if (dimensionIdx != getPriceFragmentTypeDimensionIndex())
			return super.getSelectedDimensionValue(dimensionIdx, throwExceptionIfNothingSelected);

		Collection c = priceFragmentTypeTable.getSelectedElements();
		if (c.isEmpty()) {
			if (inputPriceFragmentTypes == null)
				return null;
			else
				return inputPriceFragmentTypes.isEmpty() ? null : inputPriceFragmentTypes.get(0).getPriceFragmentTypeDimensionValue();
		}

		return ((InputPriceFragmentType)c.iterator().next()).getPriceFragmentTypeDimensionValue();
	}

	@Override
	public void setDimensionEnabled(int dimensionIdx, boolean enabled)
	{
		if (dimensionIdx != getPriceFragmentTypeDimensionIndex())
			super.setDimensionEnabled(dimensionIdx, enabled);
	}

}
