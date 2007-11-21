package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.FileListSelectionComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;

public class IssueViewComposite extends XComposite{
	
	private Label severityLbl;
	private Label severityTextLbl;
//	private Label statusLbl;
//	private Label statusTextLbl;
	private Label priorityLbl;
	private Label priorityTextLbl;
	
	private Label issueTypeLbl;
	private Label issueTypeTextLbl;

	private Label reporterLbl;
	private Label reporterTextLbl;
	
	private Label assigntoUserLbl;
	private Label assigntoUserTextLbl;
	
	private Label createTimeLbl;
	private Label createTimeTextLbl;
	private Label updateTimeLbl;
	private Label updateTimeTextLbl;
	
	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label fileLabel;
	private FileListSelectionComposite fileComposite;
	
	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;
	
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
		setLayout(new GridLayout(2, true));
		
		Group basicsGroup = new Group(this, SWT.NONE);
		basicsGroup.setText("The Basics");
		basicsGroup.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		basicsGroup.setLayoutData(gridData);
		
		XComposite basicsComposite = createBasicsComposite(basicsGroup);
		//-----------------PEOPLE GROUP----------------------------------
		Group peopleGroup = new Group(this, SWT.NONE);
		peopleGroup.setText("People");
		peopleGroup.setLayout(new GridLayout(1, true));
		gridData = new GridData(GridData.FILL_BOTH);
		peopleGroup.setLayoutData(gridData);
		
		XComposite peopleComposite = createPersonComposite(peopleGroup);
		//-----------------DATE GROUP-------------------------------------
		Group datesGroup = new Group(this, SWT.NONE);
		datesGroup.setText("Dates");
		datesGroup.setLayout(new GridLayout(1, true));
		gridData = new GridData(GridData.FILL_BOTH);
		datesGroup.setLayoutData(gridData);
		
		XComposite dateComposite = createDateComposite(datesGroup);
		//------------------------RELATIONSHIP GROUP----------------------
		Group relationshipsGroup = new Group(this, SWT.NONE);
		relationshipsGroup.setLayout(new GridLayout(1, true));
		relationshipsGroup.setText("Relationships");
		gridData = new GridData(GridData.FILL_BOTH);
		relationshipsGroup.setLayoutData(gridData);

		XComposite relationshipsComposite = createDetailComposite(relationshipsGroup);
		//--------------------------------------------------------
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		sashForm.setLayoutData(gridData);
		
		XComposite sashFormComposite = new XComposite(sashForm, SWT.NONE);
		sashFormComposite.getGridLayout().numColumns = 2;
		sashFormComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		subjectLabel = new Label(sashFormComposite, SWT.NONE);
		subjectLabel.setText("Subject:");

		subjectText = new I18nTextEditor(sashFormComposite);
		subjectText.setI18nText(issue.getSubject(), EditMode.BUFFERED);
		subjectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		descriptionLabel = new Label(sashFormComposite, SWT.NONE);
		descriptionLabel.setText("Description:");

		descriptionText = new I18nTextEditorMultiLine(sashFormComposite);
		descriptionText.setI18nText(issue.getDescription(), EditMode.BUFFERED);
		descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		fileLabel = new Label(sashFormComposite, SWT.NONE);
		fileLabel.setText("Files:");
	
		fileComposite = new FileListSelectionComposite(sashFormComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileComposite.setLayoutData(gridData);
		
		List<IssueFileAttachment> iList = issue.getFileList();
		Map<String, InputStream> iMap = new HashMap<String, InputStream>();
		for(IssueFileAttachment ia : iList){
			iMap.put(ia.getFileName(), ia.createFileAttachmentInputStream());
		}
		fileComposite.setInputStreamMap(iMap);
		
//		fileComposite.getFileInputStreamList().addAll(new Colissue.getFileList().toArray());
//		fileTextLbl = new Label(sashFormComposite, SWT.NONE);
//		issue.getFileList()
//		fileTextLbl.setText();
	}
	
	private XComposite createBasicsComposite(Composite parent){
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 2;

		issueTypeLbl = new Label(mainComposite, SWT.NONE);
		issueTypeLbl.setAlignment(SWT.RIGHT);
		issueTypeLbl.setText("Issue Type:");
		
		issueTypeTextLbl = new Label(mainComposite, SWT.NONE);
		issueTypeTextLbl.setText(issue.getIssueType().getName().getText());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		issueTypeTextLbl.setLayoutData(gridData);
		
		severityLbl = new Label(mainComposite, SWT.NONE);
		severityLbl.setAlignment(SWT.RIGHT);
		severityLbl.setText("Severity:");
		
		severityTextLbl = new Label(mainComposite, SWT.NONE);
		severityTextLbl.setText(issue.getSeverityType().getIssueSeverityTypeText().getText());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		severityTextLbl.setLayoutData(gridData);
		
//		statusLbl = new Label(mainComposite, SWT.NONE);
//		statusLbl.setAlignment(SWT.RIGHT);
//		statusLbl.setText("Status:");
//
//		statusTextLbl = new Label(mainComposite, SWT.NONE);	
//		statusTextLbl.setText(issue.getStateDefinition().getJbpmNodeName());
//		gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.grabExcessHorizontalSpace = true;
//		statusTextLbl.setLayoutData(gridData);
		
		priorityLbl = new Label(mainComposite, SWT.NONE);
		priorityLbl.setAlignment(SWT.RIGHT);
		priorityLbl.setText("Priority:");
		
		priorityTextLbl = new Label(mainComposite, SWT.NONE);
		priorityTextLbl.setText(issue.getPriority().getIssuePriorityText().getText());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		priorityTextLbl.setLayoutData(gridData);
		
//		issueTypeLbl = new Label(mainComposite, SWT.NONE);
//		issueTypeLbl.setAlignment(SWT.RIGHT);
//		issueTypeLbl.setText("Type:");
//		
//		issueTypeTextLbl = new Label(mainComposite, SWT.NONE);
//		issueTypeTextLbl.setText(issue.getStateDefinition().getJbpmNodeName());
//		gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.grabExcessHorizontalSpace = true;
//		issueTypeTextLbl.setLayoutData(gridData);
		
		return mainComposite;
	}
	
	private XComposite createPersonComposite(Composite parent){
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 2;
		/**********Person**********/
		reporterLbl = new Label(mainComposite, SWT.NONE);
		reporterLbl.setAlignment(SWT.RIGHT);
		reporterLbl.setText("Reporter:");
		
		reporterTextLbl = new Label(mainComposite, SWT.NONE);
		reporterTextLbl.setText(issue.getReporter().getName());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		reporterTextLbl.setLayoutData(gridData);
		
		assigntoUserLbl = new Label(mainComposite, SWT.NONE);
		assigntoUserLbl.setAlignment(SWT.RIGHT);
		assigntoUserLbl.setText("Assigned To:");

		assigntoUserTextLbl = new Label(mainComposite, SWT.NONE);	
		assigntoUserTextLbl.setText(issue.getAssigntoUser().getName());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		assigntoUserTextLbl.setLayoutData(gridData);
		/************************/
		return mainComposite;
	}
	
	private XComposite createDateComposite(Composite parent){
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 2;
		/**********Date**********/
		createTimeLbl = new Label(mainComposite, SWT.NONE);
		createTimeLbl.setAlignment(SWT.RIGHT);
		createTimeLbl.setText("Create Time:");
		
		createTimeTextLbl = new Label(mainComposite, SWT.NONE);
		createTimeTextLbl.setText(issue.getCreateTimestamp().toString());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		createTimeTextLbl.setLayoutData(gridData);
		
		updateTimeLbl = new Label(mainComposite, SWT.NONE);
		updateTimeLbl.setAlignment(SWT.RIGHT);
		updateTimeLbl.setText("Update Time:");
		
		updateTimeTextLbl = new Label(mainComposite, SWT.NONE);
		updateTimeTextLbl.setText(issue.getUpdateTimestamp() == null? "": issue.getUpdateTimestamp().toString());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		updateTimeTextLbl.setLayoutData(gridData);
		/************************/
		return mainComposite;
	}
	private XComposite createDetailComposite(Composite parent){
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 2;
		/**********Detail**********/
		reporterLbl = new Label(mainComposite, SWT.NONE);
		reporterLbl.setAlignment(SWT.RIGHT);
		reporterLbl.setText("Reporter:");
		
		reporterTextLbl = new Label(mainComposite, SWT.NONE);
		reporterTextLbl.setText(issue.getReporter().getName());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		reporterTextLbl.setLayoutData(gridData);
		
		assigntoUserLbl = new Label(mainComposite, SWT.NONE);
		assigntoUserLbl.setAlignment(SWT.RIGHT);
		assigntoUserLbl.setText("Assigned To:");

		assigntoUserTextLbl = new Label(mainComposite, SWT.NONE);	
		assigntoUserTextLbl.setText(issue.getAssigntoUser().getName());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		assigntoUserTextLbl.setLayoutData(gridData);
		/************************/
		return mainComposite;
	}
}
