package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.scripting.ScriptParameter;

public class ScriptParameterCreateWizardPage extends WizardHopPage {
	

	private ScriptCreateNewParameterComposite scriptParameterComposite;
	

	public ScriptCreateNewParameterComposite getScriptParameterComposite() {
		return scriptParameterComposite;
	}

	public ScriptParameterCreateWizardPage(ScriptParameter scriptparameter) {
		super(ScriptParameterCreateWizardPage.class.getName());
		
	}

	@Override
	public Control createPageContents(Composite parent) {
        scriptParameterComposite = new ScriptCreateNewParameterComposite( parent, SWT.NONE);
		
		return  scriptParameterComposite; 
	}

}
