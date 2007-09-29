package org.nightlabs.jfire.trade.legalentity.search;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.prop.datafield.RegexDataField;
import org.nightlabs.jfire.trade.legalentity.edit.LegalEntityPersonEditor;

public class LegalEntityEditorPersonRegexDataFieldEditorFactory
extends AbstractDataFieldEditorFactory
{
	/**
	 * @see org.nightlabs.jfire.base.ui.person.edit.AbstractPersonDataFieldEditorFactory#getEditorType()
	 */
	public String[] getEditorTypes() {
		return new String[] {LegalEntityPersonEditor.EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY};
	}

	@Override
	public Class getDataFieldEditorClass() {
		return LegalEntityFieldBasedRegexDataFieldEditor.class;
	}

	@Override
	public Class getPropDataFieldType() {
		return RegexDataField.class;
	}
}
