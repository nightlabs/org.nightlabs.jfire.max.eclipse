/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.ui.script;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;

/**
 * Accessor for the registration of editors to languages of JFireScripting scripts.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptEditorRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.scripting.admin.ui.scriptEditor"; //$NON-NLS-1$
	
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
		if (element.getName().equalsIgnoreCase("scriptEditor")) { //$NON-NLS-1$
			String language = element.getAttribute("scriptLanguage"); //$NON-NLS-1$
			if (language == null || "".equals(language)) //$NON-NLS-1$
				throw new EPProcessorException("scriptEditor extension has not defined the scriptLanugage attribute. Extension namespace: "+extension.getNamespace()); //$NON-NLS-1$
			String editorID = element.getAttribute("editorID"); //$NON-NLS-1$
			if (editorID == null || "".equals(editorID)) //$NON-NLS-1$
				throw new EPProcessorException("scriptEditor extension has not defined the editorID attribute. Extension namespace: "+extension.getNamespace()); //$NON-NLS-1$
			scriptEditors.put(language, editorID);
		}
	}
	
	public String getEditorID(String language) {
		checkProcessing();
		String editorID = scriptEditors.get(language);
		if (editorID == null || "".equals(editorID)) //$NON-NLS-1$
			throw new IllegalStateException("No scriptEditor was registered for the language: "+language); //$NON-NLS-1$
		return editorID;
	}
	
	private static ScriptEditorRegistry sharedInstance;
	
	public static ScriptEditorRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ScriptEditorRegistry();
		return sharedInstance;
	}

}
