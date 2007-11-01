package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueStatus;
import org.nightlabs.jfire.security.User;

public class IssueViewComposite extends XComposite{
	
	private IssueSeverityType selectedIssueSeverityType;
	private IssueStatus selectedIssueStatus;
	private IssuePriority selectedIssuePriority;
	
	private Label severityLbl;
	private Label severityTextLbl;
	private Label statusLbl;
	private Label statusTextLbl;
	private Label priorityLbl;
	private Label priorityTextLbl;

	private Label userLbl;
	private Label userTextLbl;
	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label fileLabel;
	private Label fileTextLbl;
	private Button fileButton;
	
	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;
	
	private User selectedUser;
	
	private Issue issue;
	
	public IssueViewComposite(Issue issue, Composite parent, int style) {
		super(parent, style);
		this.issue = issue;
		createComposite(this);
	}
	
	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		setLayout(new GridLayout(2, false));
		
		Group basicsGroup = new Group(this, SWT.NONE);
		basicsGroup.setText("The Basics");
		basicsGroup.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		basicsGroup.setLayoutData(gridData);
		
		XComposite basicsComposite = createBasicsComposite(basicsGroup);
		//-------------------------------------------------------
		Group peopleGroup = new Group(this, SWT.NONE);
		peopleGroup.setText("People");
		peopleGroup.setLayout(new GridLayout(1, false));
		gridData = new GridData(GridData.FILL_BOTH);
		peopleGroup.setLayoutData(gridData);
		
		XComposite peopleComposite = createPersonComposite(peopleGroup);
		//-------------------------------------------------------
		Group datesGroup = new Group(this, SWT.NONE);
		datesGroup.setText("Dates");
		gridData = new GridData(GridData.FILL_BOTH);
		datesGroup.setLayoutData(gridData);
		
		Group relationshipsGroup = new Group(this, SWT.NONE);
		relationshipsGroup.setText("Relationships");
		gridData = new GridData(GridData.FILL_BOTH);
		relationshipsGroup.setLayoutData(gridData);

		//--------------------------------------------------------
		subjectLabel = new Label(this, SWT.NONE);
		subjectLabel.setText("Subject:");

		subjectText = new I18nTextEditor(this);
		subjectText.setI18nText(issue.getSubject(), EditMode.BUFFERED);
		
		descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setText("Description:");

		descriptionText = new I18nTextEditorMultiLine(this);
		descriptionText.setI18nText(issue.getDescription(), EditMode.BUFFERED);

		gridData = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gridData);
		
		fileLabel = new Label(this, SWT.NONE);
		fileLabel.setText("Files:");

	}
	
	private XComposite createBasicsComposite(Composite parent){
		XComposite c = new XComposite(parent, SWT.NONE);
		c.getGridLayout().numColumns = 2;
		
		severityLbl = new Label(c, SWT.NONE);
		severityLbl.setAlignment(SWT.RIGHT);
		severityLbl.setText("Severity:");
		
		severityTextLbl = new Label(c, SWT.NONE);
		severityTextLbl.setText(issue.getSeverityType().getIssueSeverityTypeText().getText());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		severityTextLbl.setLayoutData(gridData);
		
		statusLbl = new Label(c, SWT.NONE);
		statusLbl.setAlignment(SWT.RIGHT);
		statusLbl.setText("Status:");

		statusTextLbl = new Label(c, SWT.NONE);	
		statusTextLbl.setText(issue.getStatus().getIssueStatusText().getText());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		statusTextLbl.setLayoutData(gridData);
		
		priorityLbl = new Label(c, SWT.NONE);
		priorityLbl.setAlignment(SWT.RIGHT);
		priorityLbl.setText("Priority:");
		
		priorityTextLbl = new Label(c, SWT.NONE);
		priorityTextLbl.setText(issue.getPriority().getIssuePriorityText().getText());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		priorityTextLbl.setLayoutData(gridData);
		
		return c;
	}
	
	private XComposite createPersonComposite(Composite parent){
		XComposite c = new XComposite(parent, SWT.NONE);
		c.getGridLayout().numColumns = 2;
		/**********USER**********/
		userLbl = new Label(c, SWT.NONE);
		userLbl.setAlignment(SWT.RIGHT);
		userLbl.setText("User:");
		
		XComposite userComposite = new XComposite(c, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		userComposite.getGridLayout().numColumns = 2;
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		userComposite.setLayoutData(gridData);
		
		userTextLbl = new Label(userComposite, SWT.NONE);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		userTextLbl.setLayoutData(gridData);
		userTextLbl.setText(issue.getUser().getName());
		
		/************************/
		return c;
	}
	
	private XComposite createDateComposite(Composite parent){
		return null;
	}
	private XComposite createDetailComposite(Composite parent){
		return null;
	}
}
