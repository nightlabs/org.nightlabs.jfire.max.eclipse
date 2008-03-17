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

package org.nightlabs.jfire.reporting.ui.config;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.util.CollectionUtil;

/**
 * A Combo showing all BIRT outputtypes supported by JFire.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class BirtOutputCombo extends XComboComposite<Birt.OutputFormat> {

	/**
	 * @param types
	 * @param parent
	 * @param style
	 */
	public BirtOutputCombo(Composite parent, int style) {
		super(parent, style);
		setInput(CollectionUtil.array2ArrayList(Birt.OutputFormat.values()));
	}

}
