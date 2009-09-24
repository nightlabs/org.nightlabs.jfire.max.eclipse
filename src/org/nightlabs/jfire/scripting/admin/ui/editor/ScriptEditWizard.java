package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;

/**
 *
 * @author vince
 *
 */

public class ScriptEditWizard extends DynamicPathWizard {

	private ScriptParameterEditWizardPage parameterCreatepage;
	private ScriptParameter scriptparameter;
	private boolean storeOnServer;
	String[] fetchGroups;


	public ScriptEditWizard(ScriptParameter scriptparameter, boolean storeOnServer, String[] fetchGroups){
		super();


		this.scriptparameter=scriptparameter;
		this.storeOnServer = storeOnServer || scriptparameter== null;
		this.fetchGroups = fetchGroups;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptEditWizard.title"));

	}

	@Override
	public void addPages() {

		parameterCreatepage= new ScriptParameterEditWizardPage(scriptparameter);

		addPage(parameterCreatepage);
	}

	@Override
	public boolean performFinish() {
		scriptparameter =parameterCreatepage.getScriptParameterComposite().getScriptParameter();
		return true;

	}

}
