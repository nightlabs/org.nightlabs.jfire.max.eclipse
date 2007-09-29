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

import org.eclipse.jface.viewers.CellEditor;
import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.jfire.accounting.book.IMoneyFlowDimension;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;

public interface MappingDimension extends IMoneyFlowDimension {

	public String getName();
	
	public String getCellEditorPropertyName();
	public CellEditor getCellEditor();

	public boolean canModify(MoneyFlowMapping mapping);
	
	public Object getValue(MoneyFlowMapping mapping);
	public String getValueText(MoneyFlowMapping mapping);
	
	public void modify(Object element, Object value);
	
	public MappingDimensionWizardPage getCreateMappingWizardPage(IDynamicPathWizard wizard);
}
