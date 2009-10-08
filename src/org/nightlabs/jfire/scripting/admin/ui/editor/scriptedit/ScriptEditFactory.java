package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit;

import org.eclipse.core.runtime.IExecutableExtension;

/**
 * Implementations provide UI specific for a certain script language.
 * TODO more documentation
 * <p>
 * <b>Important!</b> It is recommended to not directly implement this interface, but instead subclass {@link AbstractScriptEditFactory}.
 * </p>
 *
 * @author vince - vince at guinaree dot com
 *
 */
public interface ScriptEditFactory extends IExecutableExtension
{
	ScriptEdit createScriptEdit();

	String getScriptEditFactoryID();
	String getLanguage();
}
