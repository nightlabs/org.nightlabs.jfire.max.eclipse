package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.NumberDataField;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityPersonEditor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorNumberDataFieldEditorFactory
extends AbstractDataFieldEditorFactory<NumberDataField>
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorFactory#createPropDataFieldEditor(org.nightlabs.jfire.prop.IStruct, org.nightlabs.jfire.prop.DataField)
	 */
	@Override
	public DataFieldEditor<NumberDataField> createPropDataFieldEditor(IStruct struct, NumberDataField data) {
		return new LegalEntityEditorNumberDataFieldEditor(struct, data);
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
	public Class<NumberDataField> getPropDataFieldType() {
		return NumberDataField.class;
	}

}
