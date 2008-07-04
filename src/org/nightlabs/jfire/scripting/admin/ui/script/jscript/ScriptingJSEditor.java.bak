/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.ui.script.jscript;

import net.sourceforge.jseditor.editors.JSEditor;

import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptingJSEditor extends JSEditor {

	public static final String ID_EDITOR = ScriptingJSEditor.class.getName();
	
	/**
	 * 
	 */
	public ScriptingJSEditor() {
		super();
	}
	
	@Override
	protected IDocumentProvider createDocumentProvider() {
		return new ScriptingJSDocumentProvider();
	}

}
