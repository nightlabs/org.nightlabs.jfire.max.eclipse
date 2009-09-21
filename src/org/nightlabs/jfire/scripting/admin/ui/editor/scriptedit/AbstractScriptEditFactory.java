package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public abstract class AbstractScriptEditFactory
implements ScriptEditFactory
{
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_LANGUAGE = "language";

	private String scriptEditFactoryID;
	private String language;

	protected abstract ScriptEdit _createScriptEdit();

	@Override
	public ScriptEdit createScriptEdit() {
		ScriptEdit result = _createScriptEdit();
		result.setScriptEditFactory(this);
		result.init();
		return result;
	}

	@Override
	public String getScriptEditFactoryID() {
		return scriptEditFactoryID;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public void setInitializationData(IConfigurationElement element, String propertyName, Object data) throws CoreException
	{
		scriptEditFactoryID = element.getAttribute(ATTRIBUTE_ID);
		language = element.getAttribute(ATTRIBUTE_LANGUAGE);
	}
}
