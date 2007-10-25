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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import java.util.Locale;

import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.gridpriceconfig.IPriceCoordinate;
import org.nightlabs.jfire.trade.CustomerGroup;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class DimensionValue
{
	public static class CustomerGroupDimensionValue extends DimensionValue {
		private CustomerGroup customerGroup;
		public CustomerGroupDimensionValue(Dimension<CustomerGroupDimensionValue> dimension, CustomerGroup customerGroup) {
			super(dimension);
			this.customerGroup = customerGroup;
		}
		@Override
		public String getName() {
			return customerGroup.getName().getText();
		}
		@Override
		public void adjustPriceCoordinate(IPriceCoordinate priceCoordinate)
		{
			priceCoordinate.setCustomerGroupPK(customerGroup.getPrimaryKey());
		}
		@Override
		public Object getObject()
		{
			return customerGroup;
		}
	}
	public static class CurrencyDimensionValue extends DimensionValue {
		private Currency currency;
		public CurrencyDimensionValue(Dimension<CurrencyDimensionValue> dimension, Currency currency) {
			super(dimension);
			this.currency = currency;
		}
		@Override
		public String getName() {
			return currency.getCurrencySymbol();
		}
		@Override
		public void adjustPriceCoordinate(IPriceCoordinate priceCoordinate)
		{
			priceCoordinate.setCurrencyID(currency.getCurrencyID());
		}
		@Override
		public Object getObject()
		{
			return currency;
		}
	}
	public static class TariffDimensionValue extends DimensionValue {
		private Tariff tariff;
		public TariffDimensionValue(Dimension<TariffDimensionValue> dimension, Tariff tariff) {
			super(dimension);
			this.tariff = tariff;
		}
		@Override
		public String getName() {
			return tariff.getName().getText();
		}
		@Override
		public void adjustPriceCoordinate(IPriceCoordinate priceCoordinate)
		{
			priceCoordinate.setTariffPK(tariff.getPrimaryKey());
		}
		@Override
		public Object getObject()
		{
			return tariff;
		}
	}
	public static class PriceFragmentTypeDimensionValue extends DimensionValue {
		private PriceFragmentType priceFragmentType;
		public PriceFragmentTypeDimensionValue(Dimension<PriceFragmentTypeDimensionValue> dimension, PriceFragmentType priceFragmentType) {
			super(dimension);
			this.priceFragmentType = priceFragmentType;
		}
		@Override
		public String getName() {
			return priceFragmentType.getName().getText(Locale.getDefault().getLanguage());
		}
		@Override
		public void adjustPriceCoordinate(IPriceCoordinate priceCoordinate)
		{
			throw new UnsupportedOperationException("PriceFragments are inside a PriceCell and therefore not part of a PriceCoordinate!"); //$NON-NLS-1$
		}
		/**
		 * @see org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue#getObject()
		 */
		@Override
		public Object getObject()
		{
			return priceFragmentType;
		}
	}


	protected Dimension<? extends DimensionValue> dimension;
	public DimensionValue(Dimension<? extends DimensionValue> dimension) {
		this.dimension = dimension;
	}

	public abstract String getName();
	public abstract void adjustPriceCoordinate(IPriceCoordinate priceCoordinate);
	public abstract Object getObject();
}
