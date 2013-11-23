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
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMoneyFlowMapping;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class OwnerDimension implements MappingDimension {

	/**
	 * 
	 */
	public OwnerDimension() {
		super();
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#getName()
	 */
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.OwnerDimension.name"); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#getCellEditorPropertyName()
	 */
	public String getCellEditorPropertyName() {
		return "productTypeOwner"; //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#getCellEditor()
	 */
	public CellEditor getCellEditor() {
		return null;
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#canModify(org.nightlabs.jfire.accounting.book.MoneyFlowMapping)
	 */
	public boolean canModify(MoneyFlowMapping mapping) {
		return false;
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#getValue(org.nightlabs.jfire.accounting.book.MoneyFlowMapping)
	 */
	public Object getValue(MoneyFlowMapping mapping) {
		if (!(mapping instanceof PFMoneyFlowMapping))
			throw new IllegalArgumentException("OwnerDimension needs a PFMoneyFlowMapping as input for getValueText, but received: "+mapping.getClass().getName()); //$NON-NLS-1$
		PFMoneyFlowMapping pfMapping = (PFMoneyFlowMapping)mapping;
		return pfMapping.getOwner();
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#getValueText(org.nightlabs.jfire.accounting.book.MoneyFlowMapping)
	 */
	public String getValueText(MoneyFlowMapping mapping) {
		if (!(mapping instanceof PFMoneyFlowMapping))
			throw new IllegalArgumentException("OwnerDimension needs a PFMoneyFlowMapping as input for getValueText, but received: "+mapping.getClass().getName()); //$NON-NLS-1$
		PFMoneyFlowMapping pfMapping = (PFMoneyFlowMapping)mapping;
		if (pfMapping.getOwner() == null)
			return "*"; //$NON-NLS-1$
		AnchorID anchorID = (AnchorID) JDOHelper.getObjectId(pfMapping.getOwner());
		LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
				anchorID,
				new String[] {
						FetchPlan.DEFAULT,
						LegalEntity.FETCH_GROUP_PERSON,
						PropertySet.FETCH_GROUP_FULL_DATA
				}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			);
		if (legalEntity.getPerson() == null)
			return legalEntity.getPrimaryKey();

		return legalEntity.getPerson().getDisplayName();
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#modify(java.lang.Object, java.lang.Object)
	 */
	public void modify(Object element, Object value) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension#getCreateMappingWizardPage(IDynamicPathWizard)
	 */
	public MappingDimensionWizardPage getCreateMappingWizardPage(IDynamicPathWizard wizard) {
		// TODO Auto-generated method stub
		return new OwnerDimensionWizardPage(""); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.jfire.accounting.book.IMoneyFlowDimension#getMoneyFlowDimensionID()
	 */
	public String getMoneyFlowDimensionID() {
		return org.nightlabs.jfire.accounting.book.mappingbased.OwnerDimension.MONEY_FLOW_DIMENSION_ID;
	}

	/**
	 * @see org.nightlabs.jfire.accounting.book.IMoneyFlowDimension#getValues()
	 */
	public String[] getValues() {
		return null;
	}

}
