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

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddReportCategoryAction extends ReportRegistryItemAction {

	/**
	 * 
	 */
	public AddReportCategoryAction() {
		super();
	}

	/**
	 * @param text
	 */
	public AddReportCategoryAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public AddReportCategoryAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AddReportCategoryAction(String text, int style) {
		super(text, style);
	}

	public @Override void run(Collection<ReportRegistryItem> reportRegistryItems) {
		if (reportRegistryItems.size() == 1)
			AddReportCategoryWizard.show(reportRegistryItems.iterator().next());
	}

}
