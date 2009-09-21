package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.jfire.scripting.dao.ScriptParameterDAO;
import org.nightlabs.jfire.scripting.dao.ScriptParameterSetDAO;
import org.nightlabs.jfire.scripting.dao.ScriptRegistryItemDAO;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.progress.NullProgressMonitor;

public class ScriptEditWizard extends DynamicPathWizard {
	
	private ScriptParameterEditWizardPage parameterCreatepage;
	private ScriptParameter scriptparameter;
	 private boolean storeOnServer;
	 String[] fetchGroups;
	
	
	public ScriptEditWizard(ScriptParameter scriptparameter, boolean storeOnServer, String[] fetchGroups){
		super();
		
		//this.issueSeverityType = issueSeverityType;
		this.scriptparameter=scriptparameter;
		this.storeOnServer = storeOnServer || scriptparameter== null;
		this.fetchGroups = fetchGroups;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptEditWizard.title")); 
	
	}

	@Override
	public void addPages() {
		
		parameterCreatepage= new ScriptParameterEditWizardPage(scriptparameter);
		//severityTypeCreatePage = new IssueTypeSeverityTypeGeneralWizardPage(issueSeverityType);
		addPage(parameterCreatepage);
	}
	
	@Override
	public boolean performFinish() {
		          scriptparameter =parameterCreatepage.getScriptParameterComposite().getScriptParameter();
		          return true;

	}

}
