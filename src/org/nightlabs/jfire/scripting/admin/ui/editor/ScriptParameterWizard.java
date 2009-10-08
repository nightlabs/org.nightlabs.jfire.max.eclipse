package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.scripting.IScriptParameter;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.jfire.scripting.dao.ScriptParameterDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 *
 * @author vince
 *
 */

public class ScriptParameterWizard extends DynamicPathWizard {

	private Script script;
	private ScriptParameterCreateWizardPage parameterCreatepage;
	private ScriptParameter scriptparameter;
	private boolean storeOnServer;
	private String[] fetchGroups;


	public ScriptParameterWizard( boolean storeOnServer, Script script){
        super();



		this.storeOnServer = storeOnServer || scriptparameter== null;

		this.script= script;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptParameterWizard.title"));
	}

	@Override
	public void addPages() {


		parameterCreatepage= new ScriptParameterCreateWizardPage(scriptparameter);


		addPage(parameterCreatepage);
	}

	@Override
	public boolean performFinish() {


		IScriptParameter scriptParameter=script.getParameterSet().createParameter(parameterCreatepage.getScriptParameterComposite().getParameterIdText().getText());
		scriptParameter.setScriptParameterClassName(parameterCreatepage.getScriptParameterComposite().getParameterNameText().getText());

		if(storeOnServer){



			ScriptParameterDAO.sharedInstance().storeParameter(scriptParameter, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,  new NullProgressMonitor());

		}

		return scriptParameter !=null;
	}

	public Script getScript() {
		return script;
	}

}
