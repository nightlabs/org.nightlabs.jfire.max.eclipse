package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;

public class ScriptCreateNewParameterComposite extends XComposite {

	private Label parameterIdLabel;
	private Label parameterNameLabel;
	private Text parameterIdText;



	private Text parameterNameText;
	public Text getParameterNameText() {
		return parameterNameText;
	}


	public Text getParameterIdText() {
		return parameterIdText;
	}



	public ScriptCreateNewParameterComposite(Composite parent, int style) {
		super(parent, style );

		createComposite(this);
	}

	protected void createComposite(Composite parent) {
		setLayout(new GridLayout(1, false));

		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.group.ScriptCreateNewParameterComposite.EditParameter.text"));
		idGroup.setLayout(new GridLayout(1, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		parameterIdLabel = new Label(idGroup, SWT.NONE);
		parameterIdLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptCreateNewParameterComposite.label.parameterid.text"));
		parameterIdText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		parameterNameLabel = new Label(idGroup, SWT.NONE);
		parameterNameLabel.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptCreateNewParameterComposite.label.parametertype.text"));
		parameterNameText =new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		parameterIdText.setLayoutData(gridData);
		parameterNameText.setLayoutData(gridData);


		       parameterIdText.getText();
			    parameterNameText.getText();






		}
	}




