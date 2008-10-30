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

import java.util.Iterator;

import javax.jdo.FetchPlan;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.prop.edit.fieldbased.EditorStructFieldRegistry;
import org.nightlabs.jfire.base.ui.prop.edit.fieldbased.FieldBasedEditor;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.config.LegalEntityViewConfigModule;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityPersonEditor extends FieldBasedEditor {

	public static final String EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY = "field-based-disguised-legal-entity"; //$NON-NLS-1$
	
	public static final String[] FETCH_GROUPS_FULL_LE_DATA = new String[] {FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON, PropertySet.FETCH_GROUP_FULL_DATA};
	
	
	@Override
	protected GridLayout createGridLayout() {
		GridLayout result = new GridLayout();
//		result.horizontalSpacing = 0;
		result.verticalSpacing = 0;
		return result;
	}
	
	@Override
	protected boolean setLayoutDataForWrapper() {
		return true;
	}
	


	@Override
	protected GridData getGridDataForField(DataField field) {
		GridData result = null;
//		if (field.getPersonStructBlockID().equals(PersonStruct.PERSONALDATA.personStructBlockID)) {
//			result = new GridData(GridData.FILL_HORIZONTAL);
//			result.horizontalSpan = 2;
//		}
//		else if (field.getPersonStructFieldID().equals(PersonStruct.POSTADDRESS_ADDRESS.personStructFieldID) && field.getPersonStructFieldOrganisationID().equals(PersonStruct.POSTADDRESS_ADDRESS.personStructFieldOrganisationID)) {
//			result = new GridData(GridData.FILL_HORIZONTAL);
//			result.horizontalSpan = 2;
//		}
//		else if (field.getPersonStructFieldID().equals(PersonStruct.INTERNET_EMAIL.personStructFieldID) && field.getPersonStructFieldOrganisationID().equals(PersonStruct.INTERNET_EMAIL.personStructFieldOrganisationID)) {
//			result = new GridData(GridData.FILL_HORIZONTAL);
//			result.horizontalSpan = 2;
//		}
		return result;
	}

	/**
	 * 
	 */
	public LegalEntityPersonEditor() {
		super();
		setEditorType(EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY);
	}

	private boolean doSetConfiguration = true;
	
	public void refreshStructFieldConfiguration() {
		doSetConfiguration = true;
	}
	
	protected void setStructFieldConfiguration() {
		if (!doSetConfiguration)
			return;

		LegalEntityViewConfigModule cfMod = (LegalEntityViewConfigModule)ConfigUtil.getUserCfMod(
				LegalEntityViewConfigModule.class,
				new String[] {FetchPlan.DEFAULT, LegalEntityViewConfigModule.FETCH_GROUP_PERSONSTRUCTFIELDS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			);
		
		EditorStructFieldRegistry.sharedInstance().clearEditorStructFieldIDs(EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY);
		if (cfMod.getStructFields().isEmpty()) {
			cfMod.getStructFields().add(PersonStruct.PERSONALDATA_COMPANY.toString());
			cfMod.getStructFields().add(PersonStruct.PERSONALDATA_NAME.toString());
			cfMod.getStructFields().add(PersonStruct.PERSONALDATA_FIRSTNAME.toString());
			
			cfMod.getStructFields().add(PersonStruct.POSTADDRESS_ADDRESS.toString());
			cfMod.getStructFields().add(PersonStruct.POSTADDRESS_POSTCODE.toString());
			cfMod.getStructFields().add(PersonStruct.POSTADDRESS_CITY.toString());
			cfMod.getStructFields().add(PersonStruct.INTERNET_EMAIL.toString());
		}
		for (Iterator<String> iter = cfMod.getStructFields().iterator(); iter.hasNext();) {
			String structFieldID = iter.next();
			try {
				EditorStructFieldRegistry.sharedInstance().addEditorStructFieldID(EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY, new StructFieldID(structFieldID));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		doSetConfiguration = false;
	}

	@Override
	public void refreshControl() {
		setStructFieldConfiguration();
		super.refreshControl();
	}
	
	@Override
	public void disposeControl() {
		super.disposeControl();
		doSetConfiguration = true;
	}
	
	
	
	
}
