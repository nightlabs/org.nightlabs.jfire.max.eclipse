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

package org.nightlabs.jfire.reporting.ui.layout;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.layout.action.view.ViewReportLayoutAction;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class ReportLayoutView extends ReportRegistryItemTreeView {

	public static final String ID_VIEW = ReportLayoutView.class.getName();
		
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(DoubleClickEvent event) {
			ActionDescriptor ad = getContextMenuManager().getActionRegistry().getActionDescriptor(
					ViewReportLayoutAction.ID, false);
			if (ad != null) {
				ad.getAction().run();
			}
		}
	};
	
	public ReportLayoutView() {
		super();
	}

	@Override
	public void createPartContents(Composite parent) {
		super.createPartContents(parent);
		getRegistryItemTree().getTreeViewer().addDoubleClickListener(doubleClickListener);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTreeView#getActionScope()
	 */
	@Override
	public String getActionScope() {
		return ReportingPlugin.SCOPE_REPORTING;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTreeView#getNotificationZone()
	 */
	@Override
	public String getNotificationZone() {
		return ReportingPlugin.ZONE_REPORTING;
	}

}
