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

package org.nightlabs.jfire.trade.ui.legalentity.edit;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.Dialog;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonEditorWizardHop;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityEditorWizard extends DynamicPathWizard {

	public static final String WIZARD_EDITOR_DOMAIN = LegalEntityEditorWizard.class.getName(); 
	
	private LegalEntity legalEntity;
	private Person lePerson;
	private PersonEditorWizardHop editorWizardHop;
	
	public LegalEntityEditorWizard(LegalEntity legalEntity) {
		super();
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(false);
		try {
			Login.getLogin();
		} catch (LoginException e1) {			
			throw new IllegalStateException("Could not get Login"); //$NON-NLS-1$
		}
		
		this.legalEntity = legalEntity;
		if (this.legalEntity == null) {
			try {
//				PersonManager pManager = PersonManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				lePerson = new Person(Login.getLogin().getOrganisationID(), IDGenerator.nextID(PropertySet.class));
			} catch (Exception e) {
				throw new RuntimeException(e);				
			}
		}
		else
			this.lePerson = legalEntity.getPerson();
		
		StructLocal struct = StructLocalDAO.sharedInstance().getStructLocal(Person.class, LegalEntity.PROPERTY_SET_SCOPE, new NullProgressMonitor());
		if (lePerson != null)
			struct.explodePropertySet(lePerson);
		
		editorWizardHop = new PersonEditorWizardHop();
		editorWizardHop.initialise(lePerson);
		addPage(editorWizardHop.getEntryPage());
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		editorWizardHop.updatePerson();
		legalEntity = null;
		try {
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			StructLocal struct = StructLocalDAO.sharedInstance().getStructLocal(Person.class, LegalEntity.PROPERTY_SET_SCOPE, new NullProgressMonitor()); 
			struct.implodePropertySet(lePerson);
			legalEntity = tradeManager.storePersonAsLegalEntity(lePerson, true, LegalEntityPersonEditor.FETCH_GROUPS_FULL_LE_DATA, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			struct.explodePropertySet(legalEntity.getPerson());
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		return true;
	}

	public LegalEntity getLegalEntity() {
		return legalEntity;
	}
	
	public static LegalEntity open(LegalEntity legalEntity) {
		LegalEntityEditorWizard wiz = new LegalEntityEditorWizard(legalEntity);
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		if (dlg.open() == Dialog.OK)
			return wiz.getLegalEntity();
		else
			return null;
	}
}
