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

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.reporting.config.ReportLayoutAvailEntry;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * Helper methods for the {@link ReportLayout} configuration.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportConfigUtil {

	/**
	 * Returns a {@link ReportRegistryItemID} of the given type that is available for
	 * the current user, or <code>null</code> if the user canceled the operation.
	 * <p>
	 * If more than one layouts are available the user will be presented a dialog to choose the layout.
	 * Only if the dialog is shown this method might return <code>null</code> otherwise an itemID
	 * is returned or an exception will be thrown.
	 * </p>
	 * 
	 * @param reportRegistryItemType The layout type of the report layout to search.
	 * @return The default {@link ReportRegistryItemID} for the given type,
	 * 		the id of a layout choosen by the user, or <code>null</code> if the user cancels the operation.
	 */
	public static final ReportRegistryItemID getReportLayoutID(String reportRegistryItemType) {
		ReportLayoutConfigModule cfMod = (ReportLayoutConfigModule)ConfigUtil.getUserCfMod(
				ReportLayoutConfigModule.class,
				new String[] {FetchPlan.DEFAULT, ReportLayoutConfigModule.FETCH_GROUP_AVAILABLE_LAYOUTS, ReportLayoutAvailEntry.FETCH_GROUP_AVAILABLE_REPORT_LAYOUT_KEYS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			);
		ReportRegistryItemID defLayoutID = cfMod.getDefaultAvailEntry(reportRegistryItemType);
		Collection<ReportRegistryItemID> itemIDs = cfMod.getAvailEntries(reportRegistryItemType);
		ReportRegistryItemID selectedItemID = defLayoutID;
		if (selectedItemID == null && itemIDs.size() > 0) {
			selectedItemID = itemIDs.iterator().next(); // choose one if no default is set.
		}
		if (itemIDs != null && itemIDs.size() > 1) {
			ReportRegistryItem selectedItem = SelectReportLayoutDialog.openDialog(itemIDs, defLayoutID);
			if (selectedItem != null) {
				selectedItemID = (ReportRegistryItemID) JDOHelper.getObjectId(selectedItem);
			} else {
				return null;
			}
				
		}
		if (selectedItemID == null)
			throw new IllegalStateException("No default ReportLayout could be found for the category type "+reportRegistryItemType); //$NON-NLS-1$
		
		return selectedItemID;
	}

	/**
	 * Returns the id of the {@link ReportLayout} marked as default
	 * for the {@link ReportCategory} with the given reportRegistryItemType.
	 * 
	 * @param reportRegistryItemType The layout type of the default report layout to search.
	 * @return The id of the {@link ReportLayout} marked as default
	 * 		for the {@link ReportCategory} with the given reportRegistryItemType.
	 */
	public static final ReportRegistryItemID getDefaultReportLayoutID(String reportRegistryItemType) {
		ReportLayoutConfigModule cfMod = (ReportLayoutConfigModule)ConfigUtil.getUserCfMod(
				ReportLayoutConfigModule.class,
				new String[] {FetchPlan.DEFAULT, ReportLayoutConfigModule.FETCH_GROUP_AVAILABLE_LAYOUTS, ReportLayoutAvailEntry.FETCH_GROUP_AVAILABLE_REPORT_LAYOUT_KEYS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			);
		return cfMod.getDefaultAvailEntry(reportRegistryItemType);
	}
}
