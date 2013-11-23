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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.export;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;

/**
 * Action to export a layout as needed for the initialisation in the server.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class ExportReportLayoutAction extends ReportRegistryItemAction {

	/**
	 * 
	 */
	public ExportReportLayoutAction() {
		super();
	}

	/**
	 * @param text
	 */
	public ExportReportLayoutAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public ExportReportLayoutAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public ExportReportLayoutAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.layout.ReportRegistryItemAction#run(org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItem)
	 */
	public @Override void run(Collection<ReportRegistryItem> reportRegistryItems) {
		ReportLayout layout = (ReportLayout)reportRegistryItems.iterator().next();
		ExportReportLayoutDialog dlg = new ExportReportLayoutDialog(Display.getDefault().getActiveShell(), (ReportRegistryItemID) JDOHelper.getObjectId(layout));
		dlg.open();
	}
	
	@Override
	public boolean calculateEnabled(Collection<ReportRegistryItem> registryItems) {
		if (registryItems.isEmpty() || (registryItems.size() != 1))
			return false;
		ReportRegistryItem registryItem = registryItems.iterator().next();
		
		if (registryItem instanceof ReportLayout)
			return true;
		return false;
	}
}
