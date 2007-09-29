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

package org.nightlabs.jfire.trade.admin.moneyflow.fragmentbased;

import org.eclipse.jface.viewers.CellEditor;
import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMoneyFlowMapping;
import org.nightlabs.jfire.trade.admin.moneyflow.MappingDimension;
import org.nightlabs.jfire.trade.admin.moneyflow.MappingDimensionWizardPage;
import org.nightlabs.jfire.trade.admin.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SourceOrganisationDimension implements MappingDimension {

	/**
	 * 
	 */
	public SourceOrganisationDimension() {
		super();
	}

	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.moneyflow.fragmentbased.SourceOrganisationDimension.name"); //$NON-NLS-1$
	}

	public String getCellEditorPropertyName() {
		return "source-organisation"; //$NON-NLS-1$
	}

	public CellEditor getCellEditor() {
		return null;
	}

	public boolean canModify(MoneyFlowMapping mapping) {
		return false;
	}

	public Object getValue(MoneyFlowMapping mapping) {
		if (!(mapping instanceof PFMoneyFlowMapping))
			throw new IllegalArgumentException("OwnerDimension needs a PFMoneyFlowMapping as input for getValueText, but received: "+mapping.getClass().getName()); //$NON-NLS-1$
		PFMoneyFlowMapping pfMapping = (PFMoneyFlowMapping)mapping;
		return pfMapping.getSourceOrganisationID();
	}

	public String getValueText(MoneyFlowMapping mapping) {
		if (!(mapping instanceof PFMoneyFlowMapping))
			throw new IllegalArgumentException("OwnerDimension needs a PFMoneyFlowMapping as input for getValueText, but received: "+mapping.getClass().getName()); //$NON-NLS-1$
		PFMoneyFlowMapping pfMapping = (PFMoneyFlowMapping)mapping;
		return (pfMapping.getSourceOrganisationID() == null)? "*": pfMapping.getSourceOrganisationID(); //$NON-NLS-1$
	}

	public void modify(Object element, Object value) {
	}

	public MappingDimensionWizardPage getCreateMappingWizardPage(IDynamicPathWizard wizard) {
		return new SourceOrganisationDimensionWizardPage();
	}

	public String getMoneyFlowDimensionID() {
		return org.nightlabs.jfire.accounting.book.mappingbased.SourceOrganisationDimension.MONEY_FLOW_DIMENSION_ID;
	}

	public String[] getValues() {
		return null;
	}

}
