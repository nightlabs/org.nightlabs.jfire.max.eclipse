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

package org.nightlabs.jfire.trade.ui.accounting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class PriceFragmentTypeCombo extends XComposite {
	
	private static final String[] FETCH_GROUPS = new String[]{FetchPlan.DEFAULT, PriceFragmentType.FETCH_GROUP_NAME};
	private List<PriceFragmentType> priceFragmentTypes = new ArrayList<PriceFragmentType>();
	private Combo combo;
	
	public PriceFragmentTypeCombo(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo = new Combo(this, SWT.NONE);
		
		Collection<PriceFragmentType> pftCollection;
		try {
			pftCollection = AccountingUtil.getAccountingManager().getPriceFragmentTypes(null, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			return;
		}
		priceFragmentTypes.addAll(pftCollection);
		for (Iterator<PriceFragmentType> iter = priceFragmentTypes.iterator(); iter.hasNext();) {
			PriceFragmentType pft = iter.next();
			combo.add(pft.getName().getText(NLLocale.getDefault().getLanguage()));
		}
	}
	
	public PriceFragmentType getSelectedPriceFragmentType() {
		if (getCombo().getSelectionIndex() >= 0)
			return priceFragmentTypes.get(getCombo().getSelectionIndex());
		return null;
	}
	
	public void setSelectedPriceFragmentType(String priceFragmentTypePK) {
		int i = 0;
		for (Iterator<PriceFragmentType> iter = priceFragmentTypes.iterator(); iter.hasNext();) {
			PriceFragmentType cur = iter.next();
			if (cur.getPrimaryKey().equals(priceFragmentTypePK) )
				getCombo().select(i);
			i++;
		}
	}
	
	public void setSelectedCurrency(PriceFragmentType priceFragmentType) {
		setSelectedPriceFragmentType(priceFragmentType.getPrimaryKey());
	}
	
	public Combo getCombo() {
		return combo;
	}
}
