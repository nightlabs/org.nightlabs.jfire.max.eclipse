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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DimensionValueSelection implements ISelection
{
	/**
	 * key: Dimension dimension<br/>
	 * value: DimensionValue dimensionValue
	 */
	private Map<Dimension, DimensionValue> dimension2Value = new HashMap<Dimension, DimensionValue>();
	private Map<Dimension, DimensionValue> dimension2Value_ro = null;

	/**
	 * @param dimension2Value key: Dimension dimension; value: DimensionValue dimensionValue
	 */
	public DimensionValueSelection(DimensionValueSelector dimensionValueSelector)
	{
		Dimension[] dimensions = dimensionValueSelector.getDimensions();
		for (int i = 0; i < dimensions.length; ++i) {
			Dimension d = dimensions[i];
			DimensionValue v = dimensionValueSelector.getSelectedDimensionValue(i, false);
			if (v != null)
				dimension2Value.put(d, v);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return false;
	}

	public Map<Dimension, DimensionValue> getDimension2Value()
	{
		if (dimension2Value_ro == null)
			dimension2Value_ro = Collections.unmodifiableMap(dimension2Value);

		return dimension2Value_ro;
	}
}
