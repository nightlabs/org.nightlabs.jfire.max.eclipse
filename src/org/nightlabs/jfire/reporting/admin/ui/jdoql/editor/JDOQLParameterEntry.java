/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.jdoql.editor;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class JDOQLParameterEntry {

	private String name;
	private String jScript;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJScript() {
		return jScript;
	}
	public void setJScript(String script) {
		jScript = script;
	}

	public Object getValue() {
		Context context = Context.enter();
		try {
			Scriptable scope = new ImporterTopLevel(context);
			
			String sourceName = "Script";
			
			Object result = context.evaluateString(
					scope, jScript, sourceName, 1, null);

			if (result instanceof Undefined)
				result = null;
			else if (result instanceof NativeJavaObject)
				result = ((NativeJavaObject)result).unwrap();
			else if (result instanceof Boolean)
				; // fine - no conversion necessary
			else if (result instanceof Number)
				; // fine - no conversion necessary
			else if (result instanceof String)
				; // fine - no conversion necessary
			else
				throw new IllegalStateException("context.evaluateString(...) returned an object of an unknown type: " + (result == null ? null : result.getClass().getName()));

			return result;
		} finally {
			Context.exit();
		}
	}
	
}
