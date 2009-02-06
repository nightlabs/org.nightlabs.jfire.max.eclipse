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

import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.gridpriceconfig.FormulaCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.IFormulaPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.IResultPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PriceConfigGridCell
{
	private IFormulaPriceConfig formulaPriceConfig;
	private IResultPriceConfig stablePriceConfig;
	private PriceCoordinate priceCoordinate;
	private PriceFragmentType priceFragmentType;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public static final String PROPERTY_FORMULA = "formula"; //$NON-NLS-1$
	public static final String PROPERTY_FALLBACK_FORMULA = "fallbackFormula"; //$NON-NLS-1$

	/**
	 * @param formulaPriceConfig Might be null.
	 * @param stablePriceConfig Must NOT be null.
	 * @param priceCoordinate Must NOT be null.
	 * @param priceFragmentType Must NOT be null.
	 */
	public PriceConfigGridCell(
			IFormulaPriceConfig formulaPriceConfig,
			IResultPriceConfig stablePriceConfig,
			PriceCoordinate priceCoordinate,
			PriceFragmentType priceFragmentType)
	{
		if (stablePriceConfig == null)
			throw new NullPointerException("stablePriceConfig"); //$NON-NLS-1$
		if (priceCoordinate == null)
			throw new NullPointerException("priceCoordinate"); //$NON-NLS-1$
		if (priceFragmentType == null)
			throw new NullPointerException("priceFragmentType"); //$NON-NLS-1$

		this.formulaPriceConfig = formulaPriceConfig;
		this.stablePriceConfig = stablePriceConfig;
		this.priceCoordinate = priceCoordinate;
		this.priceFragmentType = priceFragmentType;
	}

	/**
	 * @return Returns the formulaPriceConfig. Might be null.
	 */
	public IFormulaPriceConfig getFormulaPriceConfig()
	{
		return formulaPriceConfig;
	}
	/**
	 * @return Returns the priceCoordinate. Never null.
	 */
	public PriceCoordinate getPriceCoordinate()
	{
		return priceCoordinate;
	}
	/**
	 * @return Returns the priceFragmentType. Never null.
	 */
	public PriceFragmentType getPriceFragmentType()
	{
		return priceFragmentType;
	}
	/**
	 * @return Returns the stablePriceConfig. Never null.
	 */
	public IResultPriceConfig getStablePriceConfig()
	{
		return stablePriceConfig;
	}

	/**
	 * Convenience method.
	 *
	 * @return Returns the <tt>PriceCell</tt> to which the coordinate points. Never returns <tt>null</tt>.
	 */
	public PriceCell getPriceCell()
	{
		return stablePriceConfig.getPriceCell(priceCoordinate, true);
	}

	public long getAmount()
	{
		return getPriceCell().getPrice().getAmount(priceFragmentType);
	}

	/**
	 * Convenience method.
	 *
	 * @return Returns <tt>null</tt> or the <tt>FormulaCell</tt> to which this cell coordinate points.
	 */
	public FormulaCell getFormulaCell()
	{
		if (formulaPriceConfig != null)
			return formulaPriceConfig.getFormulaCell(priceCoordinate, false);

		return null;
	}

	/**
	 * @return Returns the formula or <tt>null</tt> if none.
	 */
	public String getFormula()
	{
		FormulaCell formulaCell = getFormulaCell();
		if (formulaCell != null)
			return formulaCell.getFormula(priceFragmentType);

		return null;
	}

	public String getFallbackFormula()
	{
		if (formulaPriceConfig == null)
			return null;

		FormulaCell formulaCell = formulaPriceConfig.getFallbackFormulaCell(false);
		if (formulaCell == null)
			return null;

		return formulaCell.getFormula(priceFragmentType);
	}

	public void setFormula(String formula)
	{
		if (formulaPriceConfig == null)
			throw new IllegalStateException("No formulaPriceConfig assigned!"); //$NON-NLS-1$

		String oldFormula = getFormula();
		formulaPriceConfig.setFormula(priceCoordinate, priceFragmentType, formula);

		if (oldFormula != formula) // workaround for bug in firePropertyChange(..) if both are null
			propertyChangeSupport.firePropertyChange(PROPERTY_FORMULA, oldFormula, formula);
	}

	public void setFallbackFormula(String formula)
	{
		if (formulaPriceConfig == null)
			throw new IllegalStateException("No formulaPriceConfig assigned!"); //$NON-NLS-1$
		
		String oldFormula = getFallbackFormula();
		formulaPriceConfig.createFallbackFormulaCell();
		formulaPriceConfig.setFallbackFormula(priceFragmentType, formula);

		if (oldFormula != formula) // workaround for bug in firePropertyChange(..) if both are null
			propertyChangeSupport.firePropertyChange(PROPERTY_FALLBACK_FORMULA, oldFormula, formula);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
