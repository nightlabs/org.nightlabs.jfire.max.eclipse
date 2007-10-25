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

package org.nightlabs.jfire.scripting.admin.ui.script.action.add;

import javax.security.auth.login.LoginException;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.scripting.ScriptCategory;
import org.nightlabs.jfire.scripting.ScriptManager;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.ui.ScriptingPlugin;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddScriptCategoryWizard extends DynamicPathWizard {

	private ScriptRegistryItem scriptRegistryItem;
	private AddScriptRegistryItemWizardPage wizardPage;
	
	/**
	 * 
	 */
	public AddScriptCategoryWizard(ScriptRegistryItem scriptRegistryItem) {
		super();
		this.scriptRegistryItem = scriptRegistryItem;
		wizardPage = new AddScriptRegistryItemWizardPage(
				scriptRegistryItem == null ? null : scriptRegistryItem.getScriptRegistryItemType()
			);
		addPage(wizardPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ScriptManager sm = ScriptingPlugin.getScriptManager();
		if ((scriptRegistryItem != null) && (!(scriptRegistryItem instanceof ScriptCategory)))
			throw new IllegalArgumentException("Can only add a Script to a ScriptCategory instance of ScriptRegistryItem. The given scriptRegistryItem is instanceof "+((scriptRegistryItem == null)?"null":scriptRegistryItem.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$
		String scriptRegistryItemID = wizardPage.getRegistryItemID();
		String scriptRegistryItemType = wizardPage.getRegistryItemType();
		ScriptCategory category;
		try {
			category = new ScriptCategory(
					(ScriptCategory)scriptRegistryItem,
					Login.getLogin().getOrganisationID(),
					scriptRegistryItemType,
					scriptRegistryItemID
				);
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		category.getName().copyFrom(wizardPage.getI18nText());
		try {
			sm.storeRegistryItem(category, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	public static int show(ScriptRegistryItem scriptRegistryItem) {
		AddScriptCategoryWizard wizard = new AddScriptCategoryWizard(scriptRegistryItem);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		return dialog.open();
	}

}
