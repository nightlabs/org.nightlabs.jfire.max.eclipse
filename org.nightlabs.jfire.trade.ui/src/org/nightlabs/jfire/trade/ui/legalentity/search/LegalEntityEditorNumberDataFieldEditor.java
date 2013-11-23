package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.NumberDataField;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorNumberDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<NumberDataField>
{
	/**
	 * @param struct
	 * @param data
	 */
	public LegalEntityEditorNumberDataFieldEditor(IStruct struct, NumberDataField data) {
		super(struct, data);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.legalentity.search.AbstractLegalEntityEditorDataFieldEditor#getText(org.nightlabs.jfire.prop.DataField)
	 */
	@Override
	protected String getText(NumberDataField dataField)
	{
		if (dataField.getData() != null) {
			return String.valueOf(dataField.getData());
		}
		return "";
	}

}
