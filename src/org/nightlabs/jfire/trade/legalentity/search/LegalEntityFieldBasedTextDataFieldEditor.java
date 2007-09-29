/**
 * 
 */
package org.nightlabs.jfire.trade.legalentity.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.TextDataFieldComposite;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.TextDataFieldEditor;
import org.nightlabs.jfire.prop.datafield.TextDataField;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntityFieldBasedTextDataFieldEditor extends
		TextDataFieldEditor {

	private TextDataFieldComposite<TextDataField> textDataFieldComposite;
	
	/**
	 * 
	 */
	public LegalEntityFieldBasedTextDataFieldEditor() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Control createControl(Composite parent) {
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, gl);
		gl.numColumns = 2;
		
		textDataFieldComposite = new TextDataFieldComposite<TextDataField>(this, parent, SWT.NONE, this, gl) {
			@Override
			protected int getTextBorderStyle() {
				return SWT.READ_ONLY;
			}
			
			@Override
			protected Object createLabelLayoutData() {
				GridData gd = new GridData();
				gd.widthHint = 80;
				return gd;
			}
		};
		textDataFieldComposite.refresh();
		return textDataFieldComposite;
	}

	@Override
	public void doRefresh() {
		if (textDataFieldComposite != null)
			textDataFieldComposite.refresh();
	}
	
}
