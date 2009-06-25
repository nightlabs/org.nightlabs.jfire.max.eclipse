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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItemParentResolver;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.security.id.RoleID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class ActiveReportRegistryItemTreeController
extends ActiveJDOObjectTreeController<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode>
{
	private RoleID filterRoleID;

	public static final String[] DEFAULT_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ReportRegistryItem.FETCH_GROUP_NAME,
		ReportRegistryItem.FETCH_GROUP_DESCRIPTION,
		ReportRegistryItem.FETCH_GROUP_PARENT_CATEGORY_ID
	};

	/**
	 *
	 */
	public ActiveReportRegistryItemTreeController(RoleID filterRoleID) {
		this.filterRoleID = filterRoleID;
	}

	@Override
	protected ReportRegistryItemNode createNode() {
		return new ReportRegistryItemNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		return new ReportRegistryItemParentResolver();
	}

	@Override
	protected Class<ReportRegistryItem> getJDOObjectClass() {
		return ReportRegistryItem.class;
	}

	@Override
	protected Collection<ReportRegistryItem> retrieveChildren(ReportRegistryItemID parentID, ReportRegistryItem parent, ProgressMonitor monitor) {
		Collection<ReportRegistryItemID> itemIDs = null;
		if (parentID == null) {
			try {
				if (filterRoleID == null)
					itemIDs = ReportingPlugin.getReportManager().getTopLevelReportRegistryItemIDs();
				else
					itemIDs = ReportingPlugin.getReportManager().getTopLevelReportRegistryItemIDs(filterRoleID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			try {
				if (ReportCategory.class.equals(JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(parentID))) {
					if (filterRoleID == null)
						itemIDs = ReportingPlugin.getReportManager().getReportRegistryItemIDsForParent(parentID);
					else
						itemIDs = ReportingPlugin.getReportManager().getReportRegistryItemIDsForParent(parentID, filterRoleID);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (itemIDs == null) {
			return Collections.emptyList();
		}
		return ReportRegistryItemDAO.sharedInstance().getReportRegistryItems(
				new HashSet<ReportRegistryItemID>(itemIDs),
				DEFAULT_FETCH_GROUPS,
				monitor
			);
	}

	@Override
	protected Collection<ReportRegistryItem> retrieveJDOObjects(Set<ReportRegistryItemID> objectIDs, ProgressMonitor monitor) {
		return ReportRegistryItemDAO.sharedInstance().getReportRegistryItems(objectIDs, DEFAULT_FETCH_GROUPS, monitor);
	}

	@Override
	protected void sortJDOObjects(List<ReportRegistryItem> objects) {
	}

}
