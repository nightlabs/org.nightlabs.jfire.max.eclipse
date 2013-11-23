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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.CellEditor;
import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMoneyFlowMapping;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.accounting.id.PriceFragmentTypeID;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class PriceFragmentDimension implements MappingDimension {

	public PriceFragmentDimension() {
		super();
	}

	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.PriceFragmentDimension.name"); //$NON-NLS-1$
	}

	@Override
	public String getCellEditorPropertyName() {
		return "priceFragment"; //$NON-NLS-1$
	}

	@Override
	public CellEditor getCellEditor() {
		return null;
	}

	@Override
	public boolean canModify(MoneyFlowMapping mapping) {
		return false;
	}

	@Override
	public Object getValue(MoneyFlowMapping mapping) {
		return null;
	}

	@Override
	public String getValueText(MoneyFlowMapping mapping) {
		if (!(mapping instanceof PFMoneyFlowMapping))
			throw new IllegalArgumentException("PriceFragmentDimension needs a PFMoneyFlowMapping as input for getValueText, but received: "+mapping.getClass().getName()); //$NON-NLS-1$
		PFMoneyFlowMapping pfMapping = (PFMoneyFlowMapping)mapping;
		PriceFragmentTypeID typeID = (PriceFragmentTypeID) JDOHelper.getObjectId(pfMapping.getPriceFragmentType());
		return PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(
				typeID,
				new String[] {FetchPlan.DEFAULT, PriceFragmentType.FETCH_GROUP_NAME}, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			).getName().getText(NLLocale.getDefault().getLanguage());
	}
	
	@Override
	public void modify(Object element, Object value) {
	}

	@Override
	public String getMoneyFlowDimensionID() {
		return org.nightlabs.jfire.accounting.book.mappingbased.PriceFragmentDimension.MONEY_FLOW_DIMENSION_ID;
	}

	@Override
	public String[] getValues() {
		return null;
	}

	@Override
	public MappingDimensionWizardPage getCreateMappingWizardPage(IDynamicPathWizard wizard) {
		return new PriceFragmentDimensionWizardPage();
	}
}
