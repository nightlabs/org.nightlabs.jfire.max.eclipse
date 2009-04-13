package org.nightlabs.jfire.department.ui.prop;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.ExpandableBlocksEditor;
import org.nightlabs.jfire.base.ui.prop.edit.fieldbased.FieldBasedEditor;
import org.nightlabs.jfire.department.prop.DepartmentDataField;
import org.nightlabs.jfire.prop.IStruct;

public class DepartmentDataFieldEditorFactory extends AbstractDataFieldEditorFactory<DepartmentDataField>
{
	@Override
	public DataFieldEditor<DepartmentDataField> createPropDataFieldEditor(IStruct struct, DepartmentDataField data) {
		return new DepartmentDataFieldEditor(struct, data);
	}

	@Override
	public String[] getEditorTypes() {
		return new String[] {ExpandableBlocksEditor.EDITORTYPE_BLOCK_BASED_EXPANDABLE, FieldBasedEditor.EDITORTYPE_FIELD_BASED};
	}

	@Override
	public Class<DepartmentDataField> getPropDataFieldType() {
		return DepartmentDataField.class;
	}
}
