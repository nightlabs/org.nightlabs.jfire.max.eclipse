package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.I18nTextDataField;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorI18nTextDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<I18nTextDataField>
{
	/**
	 * @param struct
	 * @param data
	 */
	public LegalEntityEditorI18nTextDataFieldEditor(IStruct struct, I18nTextDataField data) {
		super(struct, data);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.legalentity.search.AbstractLegalEntityEditorDataFieldEditor#getText(org.nightlabs.jfire.prop.DataField)
	 */
	@Override
	protected String getText(I18nTextDataField dataField)
	{
		if (dataField.getI18nText() != null) {
			return  dataField.getI18nText().getText();
		}
		return "";
	}

}
