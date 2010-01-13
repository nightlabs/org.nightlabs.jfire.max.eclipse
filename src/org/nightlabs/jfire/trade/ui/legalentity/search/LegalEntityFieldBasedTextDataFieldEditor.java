/**
 *
 */
package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.edit.TextEditComposite;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.TextDataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.TextDataField;
import org.nightlabs.jfire.prop.structfield.TextStructField;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class LegalEntityFieldBasedTextDataFieldEditor extends
		TextDataFieldEditor {

	public LegalEntityFieldBasedTextDataFieldEditor(IStruct struct, TextDataField data) {
		super(struct, data);
	}
	
	@Override
	protected TextEditComposite createTextEditComposite(Composite parent) {
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, gl);
		gl.numColumns = 2;

		TextEditComposite textEditComposite = new TextEditComposite(parent, SWT.NONE, ((TextStructField) getStructField()).getLineCount()) {
			@Override
			protected int getTextBorderStyle() {
				return SWT.READ_ONLY;
			}

			@Override
			protected Object createTitleLayoutData() {
				GridData gd = new GridData();
				gd.widthHint = 80;
				return gd;
			}
		};
		return textEditComposite;
	}

//	@Override
//	public void doRefresh() {
//		if (textDataFieldComposite != null)
//			textDataFieldComposite.refresh();
//	}

}
