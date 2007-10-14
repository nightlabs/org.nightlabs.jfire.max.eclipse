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

import javax.security.auth.login.LoginException;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.reporting.ReportManager;
import org.nightlabs.jfire.reporting.admin.ui.ReportingAdminPlugin;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddReportCategoryWizard extends DynamicPathWizard {

	private ReportRegistryItem reportRegistryItem;
	private AddReportCategoryWizardPage wizardPage;
	
	/**
	 * 
	 */
	public AddReportCategoryWizard(ReportRegistryItem reportRegistryItem) {
		super();
		this.reportRegistryItem = reportRegistryItem;
		wizardPage = new AddReportCategoryWizardPage();
		addPage(wizardPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ReportManager rm = ReportingAdminPlugin.getReportManager();
		if ((reportRegistryItem != null) && (!(reportRegistryItem instanceof ReportCategory)))
			throw new IllegalArgumentException("Can only add a ReportCategory to a ReportCategory instance of ReportRegistryItem. The given reportRegistryItem is instanceof "+((reportRegistryItem == null)?"null":reportRegistryItem.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$
		String reportRegistryItemType = ""; //$NON-NLS-1$
		if (reportRegistryItem != null)
			reportRegistryItemType = reportRegistryItem.getReportRegistryItemType();
		ReportCategory category;
		String organisationID;
		try {
			organisationID = Login.getLogin().getOrganisationID();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		category = new ReportCategory(
				(ReportCategory)reportRegistryItem, organisationID, 
				reportRegistryItemType, 
				wizardPage.getReportCategoryID(), false
		);
		category.getName().copyFrom(wizardPage.getI18nText());
		try {
			rm.storeRegistryItem(category, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	public static int show(ReportRegistryItem reportRegistryItem) {
		AddReportCategoryWizard wizard = new AddReportCategoryWizard(reportRegistryItem);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		return dialog.open();
	}

}
