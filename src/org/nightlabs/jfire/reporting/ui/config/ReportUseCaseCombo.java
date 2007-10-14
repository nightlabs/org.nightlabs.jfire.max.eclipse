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
import org.nightlabs.base.ui.table.TableLabelProvider;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportUseCaseCombo extends XComboComposite<ReportUseCase> {

	private static class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int arg1) {
			return ((ReportUseCase)element).getName();
		}
		
		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}
		
	}
	
	/**
	 * @param types
	 * @param parent
	 * @param comboStyle
	 */
	public ReportUseCaseCombo(Composite parent, int comboStyle) {
		super(parent,	comboStyle, (String) null, new LabelProvider());
		setInput( ReportUseCaseRegistry.sharedInstance().getReportUseCases() );
		if (elements.size() > 0)
			selectElementByIndex(0);
	}
	
}
