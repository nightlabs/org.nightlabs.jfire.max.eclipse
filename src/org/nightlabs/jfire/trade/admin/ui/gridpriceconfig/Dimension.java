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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.AddCurrencyWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcustomergroup.AddCustomerGroupWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.AddPriceFragmentTypeWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addtariff.AddTariffWizard;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class Dimension<DV extends DimensionValue>
{
	public static final String PROPERTYCHANGEKEY_ADDDIMENSIONVALUE = "addDimensionValue"; //$NON-NLS-1$
	public static final String PROPERTYCHANGEKEY_REMOVEDIMENSIONVALUE = "removeDimensionValue"; //$NON-NLS-1$

	public static class CustomerGroupDimension extends Dimension<DimensionValue.CustomerGroupDimensionValue>
	{
		@Override
		public String getName()
		{
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension.CustomerGroupDimension.name"); //$NON-NLS-1$
		}
		@Override
		public List<DimensionValue.CustomerGroupDimensionValue> getValues()
		{
			if (getGridPriceConfig() == null)
				return new ArrayList<DimensionValue.CustomerGroupDimensionValue>();
			else {
				if (getValueCache() == null) {
					List<DimensionValue.CustomerGroupDimensionValue> l = new ArrayList<DimensionValue.CustomerGroupDimensionValue>();
					for (CustomerGroup customerGroup : getGridPriceConfig().getCustomerGroups()) {
						l.add(new DimensionValue.CustomerGroupDimensionValue(this, customerGroup));
					}
					setValueCache(l);
				}
				return getValueCache();
			}
		}
		@Override
		public void guiAddDimensionValue()
		{
			AddCustomerGroupWizard wizard = new AddCustomerGroupWizard(this);
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(
					RCPUtil.getActiveWorkbenchShell(), wizard);
			wizardDialog.open();
		}
		@Override
		public void guiFeedbackAddDimensionValue(DimensionValue.CustomerGroupDimensionValue dimensionValue)
		{
			CustomerGroup customerGroup = (CustomerGroup)dimensionValue.getObject();
			if (!((GridPriceConfig)getGridPriceConfig()).addCustomerGroup(customerGroup))
				return;

			if (getValueCache() != null)
				addValueCacheItem(dimensionValue);

			propertyChangeSupport.firePropertyChange(
					PROPERTYCHANGEKEY_ADDDIMENSIONVALUE, null, dimensionValue);
		}
	}
	public static class CurrencyDimension extends Dimension<DimensionValue.CurrencyDimensionValue>
	{
		@Override
		public String getName()
		{
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension.CurrencyDimension.name"); //$NON-NLS-1$
		}
		@Override
		public List<DimensionValue.CurrencyDimensionValue> getValues()
		{
			if (getGridPriceConfig() == null)
				return new ArrayList<DimensionValue.CurrencyDimensionValue>();
			else {
				if (getValueCache() == null) {
					List<DimensionValue.CurrencyDimensionValue> l = new ArrayList<DimensionValue.CurrencyDimensionValue>();
					for (Currency currency : getGridPriceConfig().getCurrencies()) {
						l.add(new DimensionValue.CurrencyDimensionValue(this, currency));
					}
					setValueCache(l);
				}
				return getValueCache();
			}
		}
		@Override
		public void guiAddDimensionValue()
		{
			AddCurrencyWizard wizard = new AddCurrencyWizard(this);
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(
					RCPUtil.getActiveWorkbenchShell(), wizard);
			wizardDialog.open();
		}
		@Override
		public void guiFeedbackAddDimensionValue(DimensionValue.CurrencyDimensionValue dimensionValue)
		{
			Currency currency = (Currency)dimensionValue.getObject();
			if (!getGridPriceConfig().addCurrency(currency))
				return;

			if (getValueCache() != null)
				addValueCacheItem(dimensionValue);

			propertyChangeSupport.firePropertyChange(
					PROPERTYCHANGEKEY_ADDDIMENSIONVALUE, null, dimensionValue);
		}
	}
	public static class PriceFragmentTypeDimension extends Dimension<DimensionValue.PriceFragmentTypeDimensionValue>
	{
		@Override
		public String getName()
		{
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension.PriceFragmentTypeDimension.name"); //$NON-NLS-1$
		}
		@Override
		public List<DimensionValue.PriceFragmentTypeDimensionValue> getValues()
		{
			if (getGridPriceConfig() == null)
				return new ArrayList<DimensionValue.PriceFragmentTypeDimensionValue>();
			else {
				if (getValueCache() == null) {
					List<DimensionValue.PriceFragmentTypeDimensionValue> l = new ArrayList<DimensionValue.PriceFragmentTypeDimensionValue>();
					for (PriceFragmentType pft : getGridPriceConfig().getPriceFragmentTypes()) {
						l.add(new DimensionValue.PriceFragmentTypeDimensionValue(this, pft));
					}
					Collections.sort(l, new Comparator<DimensionValue.PriceFragmentTypeDimensionValue>() {
						private String languageID = Locale.getDefault().getLanguage();
						public int compare(DimensionValue.PriceFragmentTypeDimensionValue dv0, DimensionValue.PriceFragmentTypeDimensionValue dv1)
						{
							PriceFragmentType pft0 = (PriceFragmentType) dv0.getObject();
							String name0 = pft0.getName().getText(languageID).replace('_', '!');

							PriceFragmentType pft1 = (PriceFragmentType) dv1.getObject();
							String name1 = pft1.getName().getText(languageID).replace('_', '!');

							return name0.compareTo(name1);
						}
					});
					setValueCache(l);
				}
				return getValueCache();
			}
		}
		@Override
		public void guiAddDimensionValue()
		{
			AddPriceFragmentTypeWizard wizard = new AddPriceFragmentTypeWizard(this);
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(
					RCPUtil.getActiveWorkbenchShell(), wizard);
			wizardDialog.open();
		}
		@Override
		public void guiFeedbackAddDimensionValue(DimensionValue.PriceFragmentTypeDimensionValue dimensionValue)
		{
			PriceFragmentType priceFragmentType = (PriceFragmentType)dimensionValue.getObject();
			if (!getGridPriceConfig().addPriceFragmentType(priceFragmentType))
				return;

			if (getValueCache() != null)
				addValueCacheItem(dimensionValue);

			propertyChangeSupport.firePropertyChange(
					PROPERTYCHANGEKEY_ADDDIMENSIONVALUE, null, dimensionValue);
		}
	}
	public static class TariffDimension extends Dimension<DimensionValue.TariffDimensionValue>
	{
		@Override
		public String getName()
		{
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension.TariffDimension.name"); //$NON-NLS-1$
		}
		@Override
		public List<DimensionValue.TariffDimensionValue> getValues()
		{
			if (getGridPriceConfig() == null)
				return new ArrayList<DimensionValue.TariffDimensionValue>();
			else {
				if (getValueCache() == null) {
					List<DimensionValue.TariffDimensionValue> l = new ArrayList<DimensionValue.TariffDimensionValue>();
					for (Tariff tariff : ((GridPriceConfig)getGridPriceConfig()).getTariffs()) {
						l.add(new DimensionValue.TariffDimensionValue(this, tariff));
					}
					setValueCache(l);
				}
				return getValueCache();
			}
		}
		@Override
		public void guiAddDimensionValue()
		{
			AddTariffWizard wizard = new AddTariffWizard(this);
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(
					RCPUtil.getActiveWorkbenchShell(), wizard);
			wizardDialog.open();
		}
		@Override
		public void guiFeedbackAddDimensionValue(DimensionValue.TariffDimensionValue dimensionValue)
		{
			Tariff tariff = (Tariff)dimensionValue.getObject();
			if (!getGridPriceConfig().addTariff(tariff))
				return;

			if (getValueCache() != null)
				addValueCacheItem(dimensionValue);

			propertyChangeSupport.firePropertyChange(
					PROPERTYCHANGEKEY_ADDDIMENSIONVALUE, null, dimensionValue);
		}
	}

	private GridPriceConfig gridPriceConfig = null;
	private List<DV> valueCache = null;

	public GridPriceConfig getGridPriceConfig()
	{
		return gridPriceConfig;
	}

	/**
	 * @return Returns the valueCache.
	 */
	public List<DV> getValueCache()
	{
		return valueCache;
	}
	/**
	 * @param valueCache The valueCache to set.
	 */
	public void setValueCache(List<DV> valueCache)
	{
		this.valueCache = valueCache;
	}
	
	public void addValueCacheItem(DV object)
	{
		valueCache.add(object);
	}

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void setGridPriceConfig(GridPriceConfig packagePriceConfig)
	{
		this.gridPriceConfig = packagePriceConfig;
		this.valueCache = null;
	}

	public abstract String getName();
	public abstract List<DV> getValues();

	/**
	 * This method should pop up a wizard that allows to choose a DimensionValue
	 * and add it to the current price config.
	 * <p>
	 * All guiXXX methods are meant to allow interaction with the user.
	 */
	public abstract void guiAddDimensionValue();

	/**
	 * This method should be called by the GUI elements that provide a DimensionValue selection
	 * and create and add this <tt>DimensionValue</tt>.
	 * <p>
	 * Usually, this method is called by a wizard by the
	 * {@link org.eclipse.jface.wizard.Wizard#performFinish()} method.
	 *
	 * @param dimensionValue
	 */
	public abstract void guiFeedbackAddDimensionValue(DV dimensionValue);

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	/**
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	/**
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
