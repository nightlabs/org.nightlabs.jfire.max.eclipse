package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.jfire.issue.IssuePriority;

public class IssueTypePriorityComposite 
extends XComposite{

	private I18nTextEditor priorityName;
	private Button wantedIDCheckbox;
	private Text idText;
	
	private IssuePriority issuePriority;
	
	public IssueTypePriorityComposite(IssuePriority issuePriority, Composite parent, int style) {
		super(parent, style);
		this.issuePriority = issuePriority;
		
		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) {
		setLayout(new GridLayout(1, false));

		new Label(this, SWT.NONE).setText("Priority Name: ");
		priorityName = new I18nTextEditor(this);

		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setText("Priority ID");
		idGroup.setLayout(new GridLayout(2, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite checkboxComposite = new Composite(idGroup, SWT.NONE);
		checkboxComposite.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		checkboxComposite.setLayoutData(gridData);
		wantedIDCheckbox = new Button(checkboxComposite, SWT.CHECK);
		new Label(checkboxComposite, SWT.NONE).setText("Enable user created ID.");

		new Label(idGroup, SWT.NONE).setText("ID: ");
		idText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		idText.setLayoutData(gridData);
	}
}
