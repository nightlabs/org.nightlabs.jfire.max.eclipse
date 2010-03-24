package org.nightlabs.jfire.trade.ui.legalentity.search;


import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.SelectionDataField;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityEditorSelectionDataFieldEditor
extends AbstractLegalEntityEditorDataFieldEditor<SelectionDataField>
//extends SelectionDataFieldEditor
{
	/**
	 * @param struct
	 * @param data
	 */
	public LegalEntityEditorSelectionDataFieldEditor(IStruct struct, SelectionDataField data) {
		super(struct, data);
	}

	@Override
	protected String getText(SelectionDataField dataField)
	{
		if (getDataField().getStructFieldValue() != null) {
			return getDataField().getStructFieldValue().getValueName().getText();
		}
		return "";
	}

//	private Text text;
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Control createControl(Composite parent)
//	{
//		XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);
//
//		Label label = new Label(wrapper, SWT.NONE);
//		label.setText(getDataField().getStructField().getName().getText());
//		GridData gd = new GridData();
//		gd.widthHint = LegalEntityEditorDataFieldEditorConstants.defaultLabelWidth;
//		label.setLayoutData(gd);
//
//		text = new Text(wrapper, SWT.READ_ONLY);
//		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		text.addModifyListener(getSwtModifyListener());
//
//		return wrapper;
//	}
//
//	@Override
//	public void doRefresh() {
//		if (text != null) {
//			if (getDataField().getStructFieldValue() != null) {
//				text.setText(getDataField().getStructFieldValue().getValueName().getText());
//			}
//			else {
//				text.setText("");
//			}
//		}
//	}
}
