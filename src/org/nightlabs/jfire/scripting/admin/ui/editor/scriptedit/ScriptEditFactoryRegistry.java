package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;

public class ScriptEditFactoryRegistry extends AbstractEPProcessor
{
	private static ScriptEditFactoryRegistry sharedInstance;
	public static synchronized ScriptEditFactoryRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ScriptEditFactoryRegistry();
			sharedInstance.process();
		}

		return sharedInstance;
	}

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.scripting.admin.ui.scriptEdit"; //$NON-NLS-1$

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	private Map<String, ScriptEditFactory> scriptEditFactoryID2ScriptEditFactory = new HashMap<String, ScriptEditFactory>();
	private Map<String, ScriptEditFactory> language2ScriptEditFactory = new HashMap<String, ScriptEditFactory>();

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		ScriptEditFactory scriptEditFactory = (ScriptEditFactory) element.createExecutableExtension("class");

		if (scriptEditFactory.getScriptEditFactoryID() == null)
			throw new IllegalStateException("The implementation of method ScriptEditFactory.getScriptEditFactoryID() in class " + scriptEditFactory.getClass().getName() + " returned null!");

		if (scriptEditFactory.getScriptEditFactoryID().isEmpty())
			throw new IllegalStateException("The implementation of method ScriptEditFactory.getScriptEditFactoryID() in class " + scriptEditFactory.getClass().getName() + " returned an empty String!");

		if (scriptEditFactory.getLanguage() == null)
			throw new IllegalStateException("The implementation of method ScriptEditFactory.getLanguage() in class " + scriptEditFactory.getClass().getName() + " returned null!");

		if (scriptEditFactory.getLanguage().isEmpty())
			throw new IllegalStateException("The implementation of method ScriptEditFactory.getLanguage() in class " + scriptEditFactory.getClass().getName() + " returned an empty String!");

		{
			ScriptEditFactory previouslyRegisteredFactory = scriptEditFactoryID2ScriptEditFactory.get(scriptEditFactory.getScriptEditFactoryID());
			if (previouslyRegisteredFactory != null)
				throw new IllegalStateException("Two (or more) ScriptEditFactory-extensions are registered for the same scriptEditFactoryID \"" + scriptEditFactory.getScriptEditFactoryID() + "\"!");
		}

		{
			ScriptEditFactory previouslyRegisteredFactory = language2ScriptEditFactory.get(scriptEditFactory.getLanguage());
			if (previouslyRegisteredFactory != null)
				throw new IllegalStateException("Two (or more) ScriptEditFactory-extensions are registered for the language \"" + scriptEditFactory.getLanguage() + "\"! Previously registered scriptEditFactoryID: " + previouslyRegisteredFactory.getScriptEditFactoryID());
		}

		scriptEditFactoryID2ScriptEditFactory.put(scriptEditFactory.getScriptEditFactoryID(), scriptEditFactory);
		language2ScriptEditFactory.put(scriptEditFactory.getLanguage(), scriptEditFactory);
	}

	public ScriptEditFactory getScriptEditFactoryForScriptEditFactoryID(String scriptEditFactoryID, boolean throwExceptionIfNotFound) {
		ScriptEditFactory factory = scriptEditFactoryID2ScriptEditFactory.get(scriptEditFactoryID);
		if (factory == null && throwExceptionIfNotFound)
			throw new IllegalStateException("There is no ScriptEditFactory registered for the scriptEditFactoryID \"" + scriptEditFactoryID + "\"!");

		return factory;
	}

	public ScriptEditFactory getScriptEditFactoryForLanguage(String language, boolean throwExceptionIfNotFound) {
		ScriptEditFactory factory = language2ScriptEditFactory.get(language);
		if (factory == null && throwExceptionIfNotFound)
			throw new IllegalStateException("There is no ScriptEditFactory registered for the language \"" + language + "\"!");

		return factory;
	}

}
