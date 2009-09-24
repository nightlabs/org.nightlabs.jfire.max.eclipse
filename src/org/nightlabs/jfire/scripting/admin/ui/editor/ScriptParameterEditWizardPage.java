package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;

/**
 *
 * @author vince
 *
 */

public class ScriptParameterEditWizardPage extends WizardHopPage {

	private ScriptParameter scriptparameter;
	private ScriptParameterComposite scriptParameterComposite;



	public ScriptParameterComposite getScriptParameterComposite() {
		return scriptParameterComposite;
	}

	public ScriptParameterEditWizardPage(ScriptParameter scriptparameter) {

		super(ScriptParameterEditWizardPage.class.getName(),Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptParameterEditWizardPage.title"));

		this.scriptparameter=scriptparameter;
	}

	@Override
	public Control createPageContents(Composite parent) {
		 scriptParameterComposite = new ScriptParameterComposite(scriptparameter, parent, SWT.NONE);

		return  scriptParameterComposite;

	}

}
