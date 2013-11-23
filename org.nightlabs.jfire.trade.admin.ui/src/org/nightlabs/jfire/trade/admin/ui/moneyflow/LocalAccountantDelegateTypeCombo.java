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

package org.nightlabs.jfire.trade.admin.ui.moneyflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * A Combo displaying all registered LocalAccountantDelegateTypes.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LocalAccountantDelegateTypeCombo extends XComposite {

	private Combo combo;
	private List<LocalAccountantDelegateType> types = new ArrayList<LocalAccountantDelegateType>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public LocalAccountantDelegateTypeCombo(Composite parent) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		combo = new Combo(this, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Collection<LocalAccountantDelegateType> _types = LocalAccountantDelegateRegistry.sharedInstance().getTypes();
		for (Iterator<LocalAccountantDelegateType> iter = _types.iterator(); iter.hasNext();) {
			LocalAccountantDelegateType type = iter.next();
			combo.add(type.getName());
			types.add(type);
		}
		if (types.size() > 0)
			combo.select(0);
	}
	
	public Combo getCombo() {
		return combo;
	}

	/**
	 * Get the currently selected LocalAccountantDelegateType.
	 */
	public LocalAccountantDelegateType getSelectedType() {
		if (combo.getSelectionIndex() < 0 || combo.getSelectionIndex() >= types.size())
			return null;
		return (LocalAccountantDelegateType)types.get(combo.getSelectionIndex());
	}
	
	/**
	 * Set the selected LocalAccountantDelegateType.
	 */
	public void setSelectedType(LocalAccountantDelegateType type) {
		for (int i = 0; i < types.size(); i++) {
			LocalAccountantDelegateType cType = (LocalAccountantDelegateType) types.get(i);
			if (cType.getDelegateClass().equals(type.getDelegateClass()))
				combo.select(i);
		}
	}
	
	

}
