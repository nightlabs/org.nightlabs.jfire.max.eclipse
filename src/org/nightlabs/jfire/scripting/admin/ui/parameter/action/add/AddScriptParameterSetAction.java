/**
 *
 */
package org.nightlabs.jfire.scripting.admin.ui.parameter.action.add;

import java.util.Collection;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.scripting.ScriptManagerRemote;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.admin.ui.parameter.action.ScriptParameterSetAction;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

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
	 * @see org.nightlabs.jfire.scripting.admin.ui.parameter.action.ScriptParameterSetAction#run(java.util.Collection)
	 */
	@Override
	public void run(Collection<ScriptParameterSet> scriptParameterSets) {
		ScriptManagerRemote scriptManager;
		try {
			scriptManager = JFireEjb3Factory.getRemoteBean(ScriptManagerRemote.class, Login.getLogin().getInitialContextProperties());
		} catch (LoginException e1) {
			throw new RuntimeException(e1);
		}
		I18nTextBuffer buffer = new I18nTextBuffer();
		buffer.setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.scripting.admin.ui.parameter.action.add.AddScriptParameterSetAction.i18nBuffer.defaultLanguageText"));		 //$NON-NLS-1$
		try {
			scriptManager.createParameterSet(buffer, null, -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
