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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.language.I18nTextEditorWizardPage;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddScriptRegistryItemWizardPage extends I18nTextEditorWizardPage {

	private String pItemType;
	private LabeledText registryItemID;
	private LabeledText registryItemType;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			String editLang = getTextEditor().getLanguageChooser().getLanguage().getLanguageID();
			if (editLang.equals(NLLocale.getDefault().getLanguage()))
				registryItemID.getTextControl().setText(ObjectIDUtil.makeValidIDString(getTextEditor().getEditText()));
			getWizard().getContainer().updateButtons();
		}
	};
	
	private ModifyListener genModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			getWizard().getContainer().updateButtons();
		}
	};
	
	/**
	 * @param pageName
	 * @param editorCaption
	 */
	public AddScriptRegistryItemWizardPage(String pItemType) {
		super(AddScriptRegistryItemWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.scripting.admin.ui.script.action.add.AddScriptRegistryItemWizardPage.title"), Messages.getString("org.nightlabs.jfire.scripting.admin.ui.script.action.add.AddScriptRegistryItemWizardPage.editorCaption")); //$NON-NLS-1$ //$NON-NLS-2$
		this.pItemType = pItemType;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.I18nTextEditorWizardPage#createAdditionalContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createAdditionalContents(Composite wrapper) {
		registryItemID = new LabeledText(wrapper, Messages.getString("org.nightlabs.jfire.scripting.admin.ui.script.action.add.AddScriptRegistryItemWizardPage.registryItemID.label.text")); //$NON-NLS-1$
//		registryItemID.setEnabled(false);
		getTextEditor().addModifyListener(modifyListener);
		registryItemType = new LabeledText(wrapper, Messages.getString("org.nightlabs.jfire.scripting.admin.ui.script.action.add.AddScriptRegistryItemWizardPage.registryItemType.label.text")); //$NON-NLS-1$
		if (pItemType != null && !"".equals(pItemType)) { //$NON-NLS-1$
			registryItemType.getTextControl().setText(pItemType);
			registryItemType.setEnabled(false);
		}
		registryItemID.getTextControl().addModifyListener(genModifyListener);
		registryItemType.getTextControl().addModifyListener(genModifyListener);
	}
	
	public String getRegistryItemID() {
		return registryItemID.getTextControl().getText();
	}
	
	public String getRegistryItemType() {
		return registryItemType.getTextControl().getText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return ( registryItemID.getTextControl().getText() != null && !"".equals(registryItemID.getTextControl().getText())) && //$NON-NLS-1$
						( registryItemType.getTextControl().getText() != null && !"".equals(registryItemType.getTextControl().getText())); //$NON-NLS-1$
	}
	
}
