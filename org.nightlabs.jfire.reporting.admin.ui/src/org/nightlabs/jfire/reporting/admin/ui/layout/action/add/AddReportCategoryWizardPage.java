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

import org.nightlabs.base.ui.language.I18nTextEditorWizardPage;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddReportCategoryWizardPage extends I18nTextEditorWizardPage {

	protected final String  REPORT_CATEGORY_SUFFIX = "C";
	/**
	 * @param pageName
	 * @param editorCaption
	 */
	public AddReportCategoryWizardPage() {
		super(AddReportCategoryWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportCategoryWizardPage.title"), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportCategoryWizardPage.editorCaption")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getReportCategoryID() {
		String name = getI18nText().getText(NLLocale.getDefault().getLanguage());
		return ObjectIDUtil.makeValidIDString(name) + IDGenerator.nextIDString(ReportRegistryItem.class, REPORT_CATEGORY_SUFFIX);
	}

}
