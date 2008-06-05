package org.nightlabs.jfire.prop.html.ui;

import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.ExpandableBlocksEditor;
import org.nightlabs.jfire.base.ui.prop.edit.fieldbased.FieldBasedEditor;
import org.nightlabs.jfire.prop.html.HTMLDataField;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLDataFieldEditorFactory extends AbstractDataFieldEditorFactory<HTMLDataField>
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory#getDataFieldEditorClass()
	 */
	@Override
	public Class<? extends DataFieldEditor<HTMLDataField>> getDataFieldEditorClass()
	{
		return HTMLDataFieldEditor.class;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory#getEditorTypes()
	 */
	@Override
	public String[] getEditorTypes()
	{
		return new String[] {ExpandableBlocksEditor.EDITORTYPE_BLOCK_BASED_EXPANDABLE, FieldBasedEditor.EDITORTYPE_FIELD_BASED};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory#getPropDataFieldType()
	 */
	@Override
	public Class<HTMLDataField> getPropDataFieldType()
	{
		return HTMLDataField.class;
	}

}
