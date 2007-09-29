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

package org.nightlabs.jfire.trade.admin.gridpriceconfig;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPackagePriceConfig;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface DimensionValueSelector extends ISelectionProvider
{
//	static final int DIMENSIONIDX_CUSTOMERGROUP = 0;
////	static final int DIMENSIONIDX_SALEMODE = 1;
////	static final int DIMENSIONIDX_CATEGORY = 2;
//	static final int DIMENSIONIDX_TARIFF = 1;
//	static final int DIMENSIONIDX_CURRENCY = 2;
//	static final int DIMENSIONIDX_PRICEFRAGMENTTYPE = 3;

	public static final String PROPERTYCHANGEKEY_ADDDIMENSIONVALUE = Dimension.PROPERTYCHANGEKEY_ADDDIMENSIONVALUE;
	public static final String PROPERTYCHANGEKEY_REMOVEDIMENSIONVALUE = Dimension.PROPERTYCHANGEKEY_REMOVEDIMENSIONVALUE;

	/**
	 * This method sets the <tt>GridPriceConfig</tt> from which the
	 * dimensions will be read (and into which new Dimensions will be
	 * added when the user desires to do so). This can be either an
	 * {@link IPackagePriceConfig} or an {@link IInnerPriceConfig}.
	 * If <code>null</code> is passed,
	 * the UI element should clear itself.
	 */
	void setGridPriceConfig(GridPriceConfig gridPriceConfig);

	GridPriceConfig getGridPriceConfig();

	/**
	 * @return Returns a <tt>PriceCoordinate</tt> that reflects the currently selected
	 * dimension values. If no dimension values are loaded (or none selected) for at
	 * least one dimension, this method returns <tt>null</tt>.
	 */
	PriceCoordinate preparePriceCoordinate();

	DimensionValue getSelectedDimensionValue(
			int dimensionIdx, boolean throwExceptionIfNothingSelected);

	void setDimensionEnabled(int dimensionIdx, boolean enabled);

	boolean isDimensionEnabled(int dimensionIdx);

	Dimension[] getDimensions();

	/**
	 * @return Returns an instance of <tt>DimensionValueSelection</tt>.
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	ISelection getSelection();

	void addPropertyChangeListener(PropertyChangeListener listener);
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	/**
	 * @return Return the zero-based index of the <tt>MappingDimension</tt> for the
	 *		<tt>PriceFragmentType</tt> within the result of {@link #getDimensions()}.
	 */
	int getDimensionIdxPriceFragmentType();

}
