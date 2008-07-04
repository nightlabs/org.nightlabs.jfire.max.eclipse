/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.ui.script.jscript;

import net.sourceforge.jseditor.editors.JSDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;

/**
 * DocumentProvider that stores scripts on the server when saved locally.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptingJSDocumentProvider extends JSDocumentProvider {

	/**
	 * 
	 */
	public ScriptingJSDocumentProvider() {
		super();
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		super.doSaveDocument(monitor, element, document, overwrite);
		if (element instanceof ScriptingJScriptEditorInput) {
			ScriptingJScriptEditorInput.saveScript((ScriptingJScriptEditorInput)element, document);
		}
	}
	
}
