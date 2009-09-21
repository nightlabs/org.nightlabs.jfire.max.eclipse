package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.impl.javascript;

import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.AbstractScriptEditFactory;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.ScriptEdit;

public class ScriptEditFactory extends AbstractScriptEditFactory {

	@Override
	protected ScriptEdit _createScriptEdit() {
		return new org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.impl.javascript.ScriptEdit();
	}

}
