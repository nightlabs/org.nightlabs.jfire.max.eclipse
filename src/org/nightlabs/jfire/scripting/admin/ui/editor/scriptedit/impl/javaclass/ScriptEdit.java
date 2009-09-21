package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.impl.javaclass;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.AbstractScriptEdit;

public class ScriptEdit extends AbstractScriptEdit {

	@Override
	protected Control _createControl(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("This will soon make it possible to select a java class (installed in the server).");
		return label;
	}

}
