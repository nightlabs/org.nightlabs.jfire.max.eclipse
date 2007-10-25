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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.admin.ui.layout;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.reporting.admin.ui.ReportingAdminPlugin;
import org.nightlabs.jfire.reporting.admin.ui.layout.action.edit.EditReportLayoutAction;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTreeView;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ReportLayoutView extends ReportRegistryItemTreeView
{

	public static final String ID_VIEW = ReportLayoutView.class.getName();
	
	private EditReportLayoutAction editReportLayoutAction = new EditReportLayoutAction();
	
	private IDoubleClickListener treeDoubleClickListener = new IDoubleClickListener () {

		public void doubleClick(DoubleClickEvent event) {
//			contextMenuManager.setSelectedRegistryItems(registryItemTree.getSelectedRegistryItems(), true, true);
			ReportRegistryItem item = getRegistryItemTree().getFirstSelectedElement();
			if (item instanceof ReportLayout) {
				editReportLayoutAction.run(getRegistryItemTree().getSelectedElements());				
			}
		}		
	};
	
	@Override
	public void createPartContents(Composite parent) {
		super.createPartContents(parent);
		getRegistryItemTree().getTreeViewer().addDoubleClickListener(treeDoubleClickListener);
	}

	@Override
	public String getActionScope() {
		return ReportingAdminPlugin.SCOPE_REPORTING_ADMIN;
	}

	@Override
	public String getNotificationZone() {
		return ReportingAdminPlugin.ZONE_REPORTING_ADMIN;
	}
	
}
