package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.impl.javaclass;

import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.AbstractScriptEditFactory;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.ScriptEdit;

public class ScriptEditFactory extends AbstractScriptEditFactory
{
	@Override
	public ScriptEdit _createScriptEdit() {
		return new org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.impl.javaclass.ScriptEdit();
	}
}
