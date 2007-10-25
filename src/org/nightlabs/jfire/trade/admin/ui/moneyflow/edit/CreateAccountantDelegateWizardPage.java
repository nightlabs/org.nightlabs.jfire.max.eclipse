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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.edit;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class CreateAccountantDelegateWizardPage extends DynamicPathWizardPage {
	
	private XComposite wrapper;
	private I18nTextEditor delegateName;

	/**
	 * @param pageName
	 */
	public CreateAccountantDelegateWizardPage() {
		super(CreateAccountantDelegateWizardPage.class.getName());
		setTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.CreateAccountantDelegateWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.CreateAccountantDelegateWizardPage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		delegateName = new I18nTextEditor(wrapper, Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.CreateAccountantDelegateWizardPage.delegateName.caption")); //$NON-NLS-1$
		delegateName.setI18nText(new I18nTextBuffer());
		delegateName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});
		return wrapper;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {		
		return delegateName != null && !delegateName.getI18nText().isEmpty();
	}
	
	public I18nText getDelegateName() {
		return delegateName.getI18nText();
	}
	
	public String getDelegateID() {
		return ObjectIDUtil.makeValidIDString(getDelegateName().getText(Locale.getDefault().getLanguage()), true);
	}
}
 
