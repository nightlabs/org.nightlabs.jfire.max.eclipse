package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.scripting.Script;

public abstract class AbstractScriptEdit implements ScriptEdit
{
	private Control control;

	protected abstract Control _createControl(Composite parent);

	@Override
	public final Control createControl(Composite parent) {
		control = _createControl(parent);
		return control;
	}

	@Override
	public final Control getControl() {
		return control;
	}

	@Override
	public void init() {
	}

	private ScriptEditFactory scriptEditFactory;

	@Override
	public void setScriptEditFactory(ScriptEditFactory scriptEditFactory) {
		this.scriptEditFactory = scriptEditFactory;
	}

	@Override
	public ScriptEditFactory getScriptEditFactory() {
		return scriptEditFactory;
	}

	private Script script;

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public void setScript(Script script) {
		this.script = script;
	}
}
