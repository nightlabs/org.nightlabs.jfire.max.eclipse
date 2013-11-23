package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.TextDataField;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntityEditorTextDataFieldEditor
//extends TextDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<TextDataField>
{
	public LegalEntityEditorTextDataFieldEditor(IStruct struct, TextDataField data) {
		super(struct, data);
	}

	@Override
	protected String getText(TextDataField dataField) {
		return dataField.getText();
	}

//	private TextDataFieldComposite<TextDataField> textDataFieldComposite;
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
//		textDataFieldComposite = new TextDataFieldComposite<TextDataField>(this, parent, SWT.NONE, getSwtModifyListener(), gl) {
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
