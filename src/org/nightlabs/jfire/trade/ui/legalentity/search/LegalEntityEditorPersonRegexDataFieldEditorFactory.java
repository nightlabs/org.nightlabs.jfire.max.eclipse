package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.RegexDataField;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityPersonEditor;

public class LegalEntityEditorPersonRegexDataFieldEditorFactory
extends AbstractDataFieldEditorFactory<RegexDataField>
{
	/**
	 * @see org.nightlabs.jfire.base.ui.person.edit.AbstractPersonDataFieldEditorFactory#getEditorType()
	 */
	@Override
	public String[] getEditorTypes() {
		return new String[] {LegalEntityPersonEditor.EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY};
	}

//	@Override
//	public Class getDataFieldEditorClass() {
//		return LegalEntityFieldBasedRegexDataFieldEditor.class;
//	}

	@Override
	public Class<RegexDataField> getPropDataFieldType() {
		return RegexDataField.class;
	}
	@Override
	public DataFieldEditor<RegexDataField> createPropDataFieldEditor(IStruct struct, RegexDataField data) {
		return new LegalEntityFieldBasedRegexDataFieldEditor(struct, data);
	}
}
