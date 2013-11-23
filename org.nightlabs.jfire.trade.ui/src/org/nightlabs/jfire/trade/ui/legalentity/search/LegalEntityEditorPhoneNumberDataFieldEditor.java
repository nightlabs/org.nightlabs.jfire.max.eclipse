package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.PhoneNumberDataField;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorPhoneNumberDataFieldEditor
//extends PhoneNumberDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<PhoneNumberDataField>
{
	/**
	 * @param struct
	 * @param data
	 */
	public LegalEntityEditorPhoneNumberDataFieldEditor(IStruct struct, PhoneNumberDataField data) {
		super(struct, data);
	}

	@Override
	protected String getText(PhoneNumberDataField dataField) {
		return dataField.getPhoneNumberAsString();
	}

//	private TextDataFieldComposite<PhoneNumberDataField> textDataFieldComposite;
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Control createControl(Composite parent) {
//		GridLayout gl = new GridLayout();
//		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, gl);
//		gl.numColumns = 2;
//
//		textDataFieldComposite = new TextDataFieldComposite<PhoneNumberDataField>(this, parent, SWT.NONE, getSwtModifyListener(), gl) {
//			@Override
//			protected int getTextBorderStyle() {
//				return SWT.READ_ONLY;
//			}
//			@Override
//			protected Object createTitleLayoutData() {
//				GridData gd = new GridData();
//				gd.widthHint = LegalEntityEditorDataFieldEditorConstants.defaultLabelWidth;
//				return gd;
//			}
//		};
//		textDataFieldComposite.refresh();
//		return textDataFieldComposite;
//	}
//
//	@Override
//	public void doRefresh() {
//		if (textDataFieldComposite != null)
//			textDataFieldComposite.refresh();
//	}
}
