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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.add;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddReportLayoutAction extends ReportRegistryItemAction {

	/**
	 * 
	 */
	public AddReportLayoutAction() {
		super();
	}

	/**
	 * @param text
	 */
	public AddReportLayoutAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public AddReportLayoutAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AddReportLayoutAction(String text, int style) {
		super(text, style);
	}

	@Override
	public boolean calculateEnabled(Collection<ReportRegistryItem> registryItems) {
		if (registryItems.size() != 1)
			return false;
		ReportRegistryItem item = registryItems.iterator().next();
		if (item instanceof ReportLayout) {
			try {
				item.getParentCategoryID();
			} catch (JDODetachedFieldAccessException e) {
				// can't access parent category
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void run(Collection<ReportRegistryItem> reportRegistryItems) {		
		if (reportRegistryItems.size() != 1)
			return;
		ReportRegistryItem item = reportRegistryItems.iterator().next();
		if (!(item instanceof ReportCategory)) {
			item = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(
					item.getParentCategoryID(), new String[] {FetchPlan.DEFAULT}, new NullProgressMonitor());
		}
		AddReportLayoutWizard.show(item);
	}
	
}
