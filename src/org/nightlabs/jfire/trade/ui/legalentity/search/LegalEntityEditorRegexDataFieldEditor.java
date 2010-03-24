/**
 *
 */
package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.RegexDataField;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntityEditorRegexDataFieldEditor
//extends RegexDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<RegexDataField>
{
	public LegalEntityEditorRegexDataFieldEditor(IStruct struct, RegexDataField data) {
		super(struct, data);
	}

	@Override
	protected String getText(RegexDataField dataField)
	{
		return dataField.getText();
	}

//	private TextDataFieldComposite<RegexDataField> textDataFieldComposite;
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
//		textDataFieldComposite = new TextDataFieldComposite<RegexDataField>(this, parent, SWT.NONE, getSwtModifyListener(), gl) {
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
