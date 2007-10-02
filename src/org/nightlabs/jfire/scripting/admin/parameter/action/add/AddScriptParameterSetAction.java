/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.parameter.action.add;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.scripting.ScriptManager;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.admin.parameter.action.ScriptParameterSetAction;
import org.nightlabs.jfire.scripting.ui.ScriptingPlugin;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class AddScriptParameterSetAction extends ScriptParameterSetAction {

	/**
	 * 
	 */
	public AddScriptParameterSetAction() {
		super();
	}

	/**
	 * @param text
	 */
	public AddScriptParameterSetAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public AddScriptParameterSetAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public AddScriptParameterSetAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.scripting.admin.parameter.action.ScriptParameterSetAction#run(java.util.Collection)
	 */
	@Override
	public void run(Collection<ScriptParameterSet> scriptParameterSets) {
		ScriptManager scriptManager = ScriptingPlugin.getScriptManager();
		I18nTextBuffer buffer = new I18nTextBuffer();
		buffer.setText(Locale.getDefault().getLanguage(), "New ParameterSet");		
		try {
			scriptManager.createParameterSet(buffer, null, -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
