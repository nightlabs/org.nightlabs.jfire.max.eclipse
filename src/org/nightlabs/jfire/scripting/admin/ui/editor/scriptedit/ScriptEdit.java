package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.scripting.Script;

public interface ScriptEdit
{
	/**
	 * Set the {@link ScriptEditFactory} which created this {@link ScriptEdit} instance.
	 * This method must be called by the <code>ScriptEditFactory</code> in its {@link ScriptEditFactory#createScriptEdit()} method.
	 *
	 * @param scriptEditFactory the factory having created this instance.
	 */
	void setScriptEditFactory(ScriptEditFactory scriptEditFactory);

	/**
	 * Get the {@link ScriptEditFactory} that was previously set via {@link #setScriptEditFactory(ScriptEditFactory)}.
	 *
	 * @return the factory having created this instance.
	 */
	ScriptEditFactory getScriptEditFactory();

	/**
	 *
	 */
	void init();

	Control createControl(Composite parent);
	Control getControl();

	void setScript(Script script);
	Script getScript();
}
