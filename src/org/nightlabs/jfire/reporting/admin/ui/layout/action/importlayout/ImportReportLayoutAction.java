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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.importlayout;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;

/**
 * @author  Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ImportReportLayoutAction 
extends ReportRegistryItemAction 
{
	/**
	 * 
	 */
	public ImportReportLayoutAction() {
		super();
	}

	/**
	 * @param text
	 */
	public ImportReportLayoutAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public ImportReportLayoutAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public ImportReportLayoutAction(String text, int style) {
		super(text, style);
	}
	
	@Override
	public boolean calculateEnabled(Collection<ReportRegistryItem> registryItems) {
		if (registryItems.size() != 1)
			return false;
		ReportRegistryItem item = registryItems.iterator().next();
		if (item instanceof ReportCategory) {
			return true;
		}
		return false;
	}
	
	@Override
	public void run(Collection<ReportRegistryItem> reportRegistryItems) {		
		if (reportRegistryItems.size() != 1)
			return;
		ReportRegistryItem reportCategoryItem = reportRegistryItems.iterator().next();
		
		ImportReportLayoutDialog ilg = new ImportReportLayoutDialog(Display.getDefault().getActiveShell(), reportCategoryItem);
		ilg.open();
	}
}
