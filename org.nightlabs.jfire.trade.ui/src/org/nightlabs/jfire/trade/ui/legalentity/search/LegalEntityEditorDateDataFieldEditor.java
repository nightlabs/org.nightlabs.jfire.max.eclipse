package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.DateDataField;
import org.nightlabs.jfire.prop.structfield.DateStructField;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorDateDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<DateDataField>
{
	/**
	 * @param struct
	 * @param data
	 */
	public LegalEntityEditorDateDataFieldEditor(IStruct struct, DateDataField data) {
		super(struct, data);
	}

	@Override
	protected String getText(DateDataField dataField)
	{
		if (dataField.getDate() != null)
		{
			DateStructField dateStructField = (DateStructField) dataField.getStructField();
			return DateFormatter.formatDate(dataField.getDate(), dateStructField.getDateTimeEditFlags());
		}
		return "";
	}
}
