package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;


/**
 *
 * @author vince
 *
 */
public class ScriptParameterComposite
extends XComposite{

	private Label parameterIdLabel;
	private Label parameterShowIdLabel;
	private Label parameterNameLabel;
    private Text parameterNameText;
	private ScriptParameter scriptParameter;



	public ScriptParameterComposite(ScriptParameter parameter,Composite parent, int style) {
		super(parent, style);
		this.scriptParameter=parameter;
		createComposite(this);
	}

	protected void createComposite(Composite parent) {
		setLayout(new GridLayout(1, false));
		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setLayout(new GridLayout(2, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		parameterIdLabel = new Label(idGroup, SWT.NONE);

		parameterShowIdLabel = new Label(idGroup, SWT.LEFT);
		parameterNameLabel = new Label(idGroup, SWT.NONE);
		parameterIdLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptParameterComposite.label.parameterid.text"));
        parameterShowIdLabel.setText(scriptParameter.getScriptParameterID());
		parameterNameLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptParameterComposite.label.parametertype.text"));
		parameterNameText =new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		parameterNameText.setLayoutData(gridData);

		if( scriptParameter != null){

			parameterNameText.setText(scriptParameter.getScriptParameterClassName());


		}





	}



	public ScriptParameter getScriptParameter() {

		scriptParameter.setScriptParameterClassName(parameterNameText.getText());
		return scriptParameter;
	}
}
