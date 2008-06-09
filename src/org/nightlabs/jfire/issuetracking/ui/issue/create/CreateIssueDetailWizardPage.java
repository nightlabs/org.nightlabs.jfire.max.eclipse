package org.nightlabs.jfire.issuetracking.ui.issue.create;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateIssueDetailWizardPage 
extends WizardHopPage
{
	//GUI
	private Label subjectLabel;
	private I18nTextEditor subjectText;

	private Label descriptionLabel;
	private I18nTextEditorMultiLine descriptionText;

	private Label issueTypeLbl;
	private XComboComposite<IssueType> issueTypeCombo;
	private Label issueSeverityLbl;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private Label issuePriorityLbl;
	private XComboComposite<IssuePriority> issuePriorityCombo;

	private IssueLabelProvider issueLabelProvider = new IssueLabelProvider();

	//Used objects
	private List<IssueType> issueTypes;

	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private StateDefinition selectedState;
	private IssuePriority selectedIssuePriority;

	private Issue issue;

	private static final String[] ISSUE_TYPE_FETCH_GROUPS = {
		FetchPlan.DEFAULT,
		IssueType.FETCH_GROUP_NAME,
		IssueType.FETCH_GROUP_ISSUE_SEVERITY_TYPES,
		IssueType.FETCH_GROUP_ISSUE_PRIORITIES,
		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME
	};


	public CreateIssueDetailWizardPage(Issue issue) {
		super(CreateIssueDetailWizardPage.class.getName(), "Create Issue", SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateIssueWizard.class));
		setDescription("Enter subject & description for the issue.");
		this.issue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 6;
		
		issueTypeLbl = new Label(mainComposite, SWT.NONE);
		issueTypeLbl.setText("Issue Type: ");
		issueTypeCombo = new XComboComposite<IssueType>(mainComposite, SWT.NONE, issueLabelProvider);
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueType = issueTypeCombo.getSelectedElement();
				issue.setIssueType(selectedIssueType);

				issueSeverityCombo.removeAll();
				for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
					issueSeverityCombo.addElement(is);
				}
				issueSeverityCombo.selectElementByIndex(0);
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();

				issuePriorityCombo.removeAll();
				for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
					issuePriorityCombo.addElement(ip);
				}
				issuePriorityCombo.selectElementByIndex(0);
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
			}
		});

		issueSeverityLbl = new Label(mainComposite, SWT.NONE);
		issueSeverityLbl.setText("Severity: ");
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(mainComposite, SWT.NONE, issueLabelProvider);
		issueSeverityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
				issue.setIssueSeverityType(selectedIssueSeverityType);
			}
		});

		issuePriorityLbl = new Label(mainComposite, SWT.NONE);
		issuePriorityLbl.setText("Priority: ");
		issuePriorityCombo = new XComboComposite<IssuePriority>(mainComposite, SWT.NONE, issueLabelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
				issue.setIssuePriority(selectedIssuePriority);
			}
		});

		subjectLabel = new Label(mainComposite, SWT.NONE);
		subjectLabel.setText("Subject: ");

		subjectText = new I18nTextEditor(mainComposite);
		subjectText.setI18nText(issue.getSubject(), EditMode.DIRECT);
		subjectText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 5;
		subjectText.setLayoutData(gridData);

		descriptionLabel = new Label(mainComposite, SWT.NONE);
		descriptionLabel.setText("Description: ");

		descriptionText = new I18nTextEditorMultiLine(mainComposite, subjectText.getLanguageChooser());
		descriptionText.setI18nText(issue.getDescription(), EditMode.DIRECT);
		descriptionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 100;
		gridData.horizontalSpan = 6;
		descriptionText.setLayoutData(gridData);

		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception
			{
				issueTypes = IssueTypeDAO.sharedInstance().getAllIssueTypes(ISSUE_TYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						issueTypeCombo.removeAll();
						for (IssueType issueType : issueTypes) {
							issueTypeCombo.addElement(issueType);
						}
						issueTypeCombo.selectElementByIndex(0);
						selectedIssueType = issueTypeCombo.getSelectedElement();
						issue.setIssueType(selectedIssueType);

						issueSeverityCombo.removeAll();
						for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
							issueSeverityCombo.addElement(is);
						}
						issueSeverityCombo.selectElementByIndex(0);
						selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
						issue.setIssueSeverityType(selectedIssueSeverityType);

						issuePriorityCombo.removeAll();
						for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
							issuePriorityCombo.addElement(ip);
						}
						issuePriorityCombo.selectElementByIndex(0);
						selectedIssuePriority = issuePriorityCombo.getSelectedElement();
						issue.setIssuePriority(selectedIssuePriority);
					}
				});

				return Status.OK_STATUS;
			} 
		};
		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();

		return mainComposite;
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
	
	@Override
	public boolean isPageComplete() {
		boolean result = true;
		setErrorMessage(null);
		if (subjectText.getEditText().equals("") || subjectText.getI18nText().getText() == null) {
			setErrorMessage("Subject should not be null.");
			result = false;
		}
		
		if (descriptionText.getEditText().equals("") || descriptionText.getI18nText().getText() == null) {
			setErrorMessage("Description should not be null.");
			result = false;
		}
		
		return result;
	}
	
	public I18nTextEditorMultiLine getDescriptionText() {
		return descriptionText;
	}

	public I18nTextEditor getSubjectText() {
		return subjectText;
	}

	public IssueSeverityType getSelectedIssueSeverityType() {
		return selectedIssueSeverityType;
	}

	public void setSelectedIssueSeverityType(
			IssueSeverityType selectedIssueSeverityType) {
		this.selectedIssueSeverityType = selectedIssueSeverityType;
	}

	public StateDefinition getSelectedState() {
		return selectedState;
	}

	public void setSelectedState(StateDefinition selectedState) {
		this.selectedState = selectedState;
	}

	public IssuePriority getSelectedIssuePriority() {
		return selectedIssuePriority;
	}

	public void setSelectedIssuePriority(IssuePriority selectedIssuePriority) {
		this.selectedIssuePriority = selectedIssuePriority;
	}

	public IssueType getSelectedIssueType(){
		return selectedIssueType;
	}
}
