package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.I18nTextDataField;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityPersonEditor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorI18nTextDataFieldEditorFactory
extends AbstractDataFieldEditorFactory<I18nTextDataField>
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorFactory#createPropDataFieldEditor(org.nightlabs.jfire.prop.IStruct, org.nightlabs.jfire.prop.DataField)
	 */
	@Override
	public DataFieldEditor<I18nTextDataField> createPropDataFieldEditor(IStruct struct, I18nTextDataField data) {
		return new LegalEntityEditorI18nTextDataFieldEditor(struct, data);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorFactory#getEditorTypes()
	 */
	@Override
	public String[] getEditorTypes() {
		return new String[] {LegalEntityPersonEditor.EDITORTYPE_FIELD_BASED_DISGUISED_LEGALENTITY};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorFactory#getPropDataFieldType()
	 */
	@Override
	public Class<I18nTextDataField> getPropDataFieldType() {
		return I18nTextDataField.class;
	}

}
