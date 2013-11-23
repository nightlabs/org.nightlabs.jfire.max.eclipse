package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.edit.IEntryEditor;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.IStruct;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractLegalEntityEditorDataFieldEditor<F extends DataField>
extends AbstractDataFieldEditor<F>
implements IEntryEditor
{
	/**
	 * @param struct
	 * @param data
	 */
	public AbstractLegalEntityEditorDataFieldEditor(IStruct struct, F data) {
		super(struct, data);
	}

	private Text text;
	private XComposite wrapper;
	private Label label;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createControl(Composite parent)
	{
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);

		label = new Label(wrapper, SWT.NONE);
		label.setText(getDataField().getStructField().getName().getText());
		GridData gd = new GridData();
		gd.widthHint = LegalEntityEditorDataFieldEditorConstants.defaultLabelWidth;
		label.setLayoutData(gd);

		text = new Text(wrapper, SWT.READ_ONLY);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(getSwtModifyListener());

		return wrapper;
	}

	@Override
	public Control getControl() {
		return wrapper;
	}

	@Override
	public void doRefresh() {
		if (text != null) {
			String textValue = getText(getDataField());
			if (textValue != null) {
				text.setText(textValue);
			} else {
				text.setText("");
			}
		}
	}

	@Override
	public void updatePropertySet() {
		// do nothing, as this data field editors are only for display and not for editing
	}

	/**
	 *
	 * @param dataField the {@link DataField} to return its content as String
	 * @return the String for the {@link DataField}
	 */
	protected abstract String getText(F dataField);
	
	protected IEntryEditor getEntryViewer() {
		return this;
	}

	@Override
	public void setEnabledState(boolean enabled, String tooltip) {
		wrapper.setEnabled(enabled);
		wrapper.setToolTipText(tooltip);
	}

	@Override
	public void setTitle(String title) {
		label.setText(title);
	}
	
}
