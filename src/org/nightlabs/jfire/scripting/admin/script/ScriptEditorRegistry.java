/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.script;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * Accessor for the registration of editors to languages of JFireScripting scripts.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptEditorRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.scripting.admin.scriptEditor";
	
	/**
	 * key: language
	 * value: editorID
	 */
	private Map<String, String> scriptEditors = new HashMap<String, String>();
	
	/**
	 * 
	 */
	public ScriptEditorRegistry() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {	
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equalsIgnoreCase("scriptEditor")) {
			String language = element.getAttribute("scriptLanguage");
			if (language == null || "".equals(language))
				throw new EPProcessorException("scriptEditor extension has not defined the scriptLanugage attribute. Extension namespace: "+extension.getNamespace());
			String editorID = element.getAttribute("editorID");
			if (editorID == null || "".equals(editorID))
				throw new EPProcessorException("scriptEditor extension has not defined the editorID attribute. Extension namespace: "+extension.getNamespace());
			scriptEditors.put(language, editorID);
		}
	}
	
	public String getEditorID(String language) {
		checkProcessing();
		String editorID = scriptEditors.get(language);
		if (editorID == null || "".equals(editorID))
			throw new IllegalStateException("No scriptEditor was registered for the language: "+language);
		return editorID;
	}
	
	private static ScriptEditorRegistry sharedInstance;
	
	public static ScriptEditorRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ScriptEditorRegistry();
		return sharedInstance;
	}

}
