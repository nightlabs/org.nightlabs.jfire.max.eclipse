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
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.RegexDataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.datafield.RegexDataField;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntityFieldBasedRegexDataFieldEditor extends RegexDataFieldEditor {

	public LegalEntityFieldBasedRegexDataFieldEditor(IStruct struct, RegexDataField data) {
		super(struct, data);
	}


	@Override
	public TextEditComposite createComposite(Composite parent) {
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, gl);
		gl.numColumns = 2;

		TextEditComposite textEditComposite = new TextEditComposite(parent, SWT.NONE, 1) {
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
}
