package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;

public class IssueTypeCreateComposite
extends XComposite{

	private I18nTextEditor issueTypeText;
	private I18nTextEditor issueSeverityTypeText;
	private I18nTextEditor issuePriorityText;

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

		new Label(this, SWT.NONE).setText("Issue Type: ");
		issueTypeText = new I18nTextEditor(this);

		new Label(this, SWT.NONE).setText("Severity Type: ");
		issueSeverityTypeText = new I18nTextEditor(this);
		
		new Label(this, SWT.NONE).setText("Priority: ");
		issuePriorityText = new I18nTextEditor(this);
	}
}