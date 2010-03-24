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

package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.TextDataField;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityPersonEditor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityEditorTextDataFieldEditorFactory
extends AbstractDataFieldEditorFactory<TextDataField> {

	/**
	 * @see org.nightlabs.jfire.base.ui.person.edit.AbstractPersonDataFieldEditorFactory#getEditorType()
	 */
	@Override
	public String[] getEditorTypes() {
		return new String[] {LegalEntityPersonEditor.EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY};
	}

	@Override
	public Class<TextDataField> getPropDataFieldType() {
		return TextDataField.class;
	}

	@Override
	public DataFieldEditor<TextDataField> createPropDataFieldEditor(IStruct struct, TextDataField data) {
		return new LegalEntityEditorTextDataFieldEditor(struct, data);
	}
}
