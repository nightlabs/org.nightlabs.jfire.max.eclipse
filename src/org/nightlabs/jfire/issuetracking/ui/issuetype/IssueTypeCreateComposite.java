package org.nightlabs.jfire.issuetracking.ui.issuetype;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;

public class IssueTypeCreateComposite
extends XComposite{

	private Label issueTypeNameLbl;
	private I18nTextEditor issueTypeText;

	public IssueTypeCreateComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		setLayout(new GridLayout(2, false));

		int textStyle = SWT.READ_ONLY | SWT.BORDER;
		
		issueTypeNameLbl = new Label(this, SWT.NONE);
		issueTypeText = new I18nTextEditor(this);
		
		Group issueSeverityTypeGroup = new Group(this, SWT.NONE);
		issueSeverityTypeGroup.setText("Severity Types");
		
		List issueSeverityList = new List(issueSeverityTypeGroup, SWT.BORDER);
		
		Group issuePriorityGroup = new Group(this, SWT.NONE);
		issuePriorityGroup.setText("Priorities");
		
		List issuePriorityList = new List(issuePriorityGroup, SWT.BORDER);
	}
}
