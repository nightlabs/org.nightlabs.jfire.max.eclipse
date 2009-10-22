package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssuePropertyComposite
extends XComposite
{
	private Collection<Issue> issues;
	/**
	 * @param parent -the parent composite
	 * @param style - the SWT style flag
	 */
	public IssuePropertyComposite(Collection<Issue> issues, Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		this.issues = issues;
		createComposite();
	}

	private Label issueTypeLabel;
	private Label priorityLabel;
	private Label severityTypeLabel;
	private Label resolutionLabel;

	private XComboComposite<IssueType> issueTypeCombo;
	private XComboComposite<IssuePriority> priorityCombo;
	private XComboComposite<IssueSeverityType> severityTypeCombo;
	private XComboComposite<IssueResolution> resolutionCombo;

	private void createComposite() {
		getGridLayout().numColumns = 1;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;

		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER);
		mainComposite.getGridLayout().numColumns = 4;

		//Issue Type
		XComposite issueTypeComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		issueTypeComposite.setLayoutData(gridData);

		issueTypeLabel = new Label(issueTypeComposite, SWT.NONE);
		issueTypeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyComposite.issueType.text")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		issueTypeLabel.setLayoutData(gridData);

		issueTypeCombo = new XComboComposite<IssueType>(issueTypeComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		issueTypeCombo.setLayoutData(gridData);
		issueTypeCombo.setLabelProvider(labelProvider);
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedIssueType = issueTypeCombo.getSelectedElement();
				setIssueType(selectedIssueType);
			}
		});

		//Priority
		XComposite priorityComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		priorityComposite.setLayoutData(gridData);

		priorityLabel = new Label(priorityComposite, SWT.NONE);
		priorityLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyComposite.issuePriority.text")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		priorityLabel.setLayoutData(gridData);

		priorityCombo = new XComboComposite<IssuePriority>(priorityComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		priorityCombo.setLayoutData(gridData);
		priorityCombo.setLabelProvider(labelProvider);
		priorityCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedIssuePriority = priorityCombo.getSelectedElement();
			}
		});

		//Severity Type
		XComposite severityTypeComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		severityTypeComposite.setLayoutData(gridData);

		severityTypeLabel = new Label(severityTypeComposite, SWT.NONE);
		severityTypeLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyComposite.issueSeverityType.text")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		severityTypeLabel.setLayoutData(gridData);

		severityTypeCombo = new XComboComposite<IssueSeverityType>(severityTypeComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		severityTypeCombo.setLayoutData(gridData);
		severityTypeCombo.setLabelProvider(labelProvider);
		severityTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedIssueSeverityType = severityTypeCombo.getSelectedElement();
			}
		});

		//Resolution
		XComposite resolutionComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		resolutionComposite.setLayoutData(gridData);

		resolutionLabel = new Label(resolutionComposite, SWT.NONE);
		resolutionLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyComposite.issueResolution.text")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		resolutionLabel.setLayoutData(gridData);

		resolutionCombo = new XComboComposite<IssueResolution>(resolutionComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		resolutionCombo.setLayoutData(gridData);
		resolutionCombo.setLabelProvider(labelProvider);
		resolutionCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedIssueResolution = resolutionCombo.getSelectedElement();
			}
		});

		Job job = new Job("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyComposite.job") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyComposite.beginTask"), 100); //$NON-NLS-1$
				issueTypes = IssueTypeDAO.sharedInstance().getAllIssueTypes(ISSUE_TYPE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (issues != null) {
							findConflictValues(issues);
							if (isIssueTypeConflict) {
								issueTypeLabel.setText(issueTypeLabel.getText() + "*");
								issueTypeLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
								//								issueTypeCombo.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
							}
							if (isIssuePriorityConflict) {
								priorityLabel.setText(priorityLabel.getText() + "*");
								priorityLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
								//								priorityCombo.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
							}
							if (isIssueSeverityTypeConflict) {
								severityTypeLabel.setText(severityTypeLabel.getText() + "*");
								severityTypeLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
								//								severityTypeCombo.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
							}
							if (isIssueResolutionConflict) {
								resolutionLabel.setText(resolutionLabel.getText() + "*");
								resolutionLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
								//								resolutionCombo.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
							}
						}

						// IssueType
						issueTypeCombo.removeAll();
						IssueType defaultIssueType = null;
						for (IssueType issueType : issueTypes) {
							if (issueType.getIssueTypeID().equals(IssueType.DEFAULT_ISSUE_TYPE_ID))
								defaultIssueType = issueType;
							issueTypeCombo.addElement(issueType);
						}

						if (defaultIssueType != null && !isIssueTypeConflict)
							issueTypeCombo.selectElement(defaultIssueType);
						else
							issueTypeCombo.selectElementByIndex(0);

						selectedIssueType = issueTypeCombo.getSelectedElement();
						setIssueType(selectedIssueType);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	private List<IssueType> issueTypes;

	private IssueType selectedIssueType;
	private IssuePriority selectedIssuePriority;
	private IssueSeverityType selectedIssueSeverityType;
	private IssueResolution selectedIssueResolution;

	private static final String[] ISSUE_TYPE_FETCH_GROUPS = {
		FetchPlan.DEFAULT,
		IssueType.FETCH_GROUP_NAME,
		IssueType.FETCH_GROUP_ISSUE_SEVERITY_TYPES,
		IssueType.FETCH_GROUP_ISSUE_PRIORITIES,
		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_NAME
	};

	private void setIssueType(IssueType issueType) {
		this.selectedIssueType = issueType;
		// IssuePriority
		priorityCombo.removeAll();
		IssuePriority defaultIssuePriority = null;
		if (selectedIssueType != null) {
			for (IssuePriority priority : selectedIssueType.getIssuePriorities()) {
				if (priority.getIssuePriorityID().equals(Issue.DEFAULT_ISSUE_PRIORITY_ID))
					defaultIssuePriority = priority;
				priorityCombo.addElement(priority);
			}

			if (defaultIssuePriority != null && !isIssuePriorityConflict)
				priorityCombo.selectElement(defaultIssuePriority);
			else
				priorityCombo.selectElementByIndex(0);
		}
		selectedIssuePriority = priorityCombo.getSelectedElement();

		// IssueSeverity
		severityTypeCombo.removeAll();
		IssueSeverityType defaultIssueSeverityType = null;
		if (selectedIssueType != null) {
			for (IssueSeverityType severityType : selectedIssueType.getIssueSeverityTypes()) {
				if (severityType.getIssueSeverityTypeID().equals(Issue.DEFAULT_ISSUE_SEVERITY_TYPE_ID))
					defaultIssueSeverityType = severityType;
				severityTypeCombo.addElement(severityType);
			}

			if (defaultIssueSeverityType != null && !isIssueSeverityTypeConflict)
				severityTypeCombo.selectElement(defaultIssueSeverityType);
			else
				severityTypeCombo.selectElementByIndex(0);
		}
		selectedIssueSeverityType = severityTypeCombo.getSelectedElement();

		//IssueResolution
		IssueResolution defaultIssueResolution = null;
		if (selectedIssueType != null) {
			for (IssueResolution resolution : selectedIssueType.getIssueResolutions()) {
				resolutionCombo.addElement(resolution);
				if (resolution.getIssueResolutionID().equals(Issue.DEFAULT_ISSUE_RESOLUTION_ID))
					defaultIssueResolution = resolution;
			}

			if (defaultIssueResolution != null && !isIssueResolutionConflict)
				resolutionCombo.selectElement(defaultIssueResolution);
			else
				resolutionCombo.selectElementByIndex(0);

		}
		selectedIssueResolution = resolutionCombo.getSelectedElement();
	}

	private LabelProvider labelProvider = new LabelProvider() {
		@Override
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IssueType) {
				IssueType issueType = (IssueType) element;
				return issueType.getName().getText();
			}

			else if (element instanceof IssuePriority) {
				IssuePriority issuePriority = (IssuePriority) element;
				return issuePriority.getIssuePriorityText().getText();
			}

			else if (element instanceof IssueSeverityType) {
				IssueSeverityType issueSeverityType = (IssueSeverityType) element;
				return issueSeverityType.getIssueSeverityTypeText().getText();
			}

			else if (element instanceof IssueResolution) {
				IssueResolution issueResolution = (IssueResolution) element;
				return issueResolution.getName().getText();
			}

			return "";
		}
	};

	public IssueType getSelectedIssueType() {
		return selectedIssueType;
	}

	public IssuePriority getSelectedIssuePriority() {
		return selectedIssuePriority;
	}

	public IssueSeverityType getSelectedIssueSeverityType() {
		return selectedIssueSeverityType;
	}

	public IssueResolution getSelectedIssueResolution() {
		return selectedIssueResolution;
	}

	private boolean isIssueTypeConflict = false;
	private boolean isIssuePriorityConflict = false;
	private boolean isIssueSeverityTypeConflict = false;
	private boolean isIssueResolutionConflict = false;

	private void findConflictValues(Collection<Issue> issues) {
		IssueType lastIssueType = null;
		IssuePriority lastIssuePriority = null;
		IssueSeverityType lastIssueSeverityType = null;
		IssueResolution lastIssueResolution = null;
		boolean isFirst = true;
		for (Issue issue : issues) {
			if (isFirst) {
				lastIssueType = issue.getIssueType();
				lastIssuePriority = issue.getIssuePriority();
				lastIssueSeverityType = issue.getIssueSeverityType();
				lastIssueResolution = issue.getIssueResolution();
				isFirst = false;
			}
			else {
				isIssueTypeConflict |= !lastIssueType.getIssueTypeID().equals(issue.getIssueType().getIssueTypeID());
				isIssuePriorityConflict |= !lastIssuePriority.getIssuePriorityID().equals(issue.getIssuePriority().getIssuePriorityID());
				isIssueSeverityTypeConflict |= !lastIssueSeverityType.getIssueSeverityTypeID().equals(issue.getIssueSeverityType().getIssueSeverityTypeID());
				isIssueResolutionConflict |= !lastIssueResolution.getIssueResolutionID().equals(issue.getIssueResolution().getIssueResolutionID());

				lastIssueType = issue.getIssueType();
				lastIssuePriority = issue.getIssuePriority();
				lastIssueSeverityType = issue.getIssueSeverityType();
				lastIssueResolution = issue.getIssueResolution();
			}
		}
	}
}