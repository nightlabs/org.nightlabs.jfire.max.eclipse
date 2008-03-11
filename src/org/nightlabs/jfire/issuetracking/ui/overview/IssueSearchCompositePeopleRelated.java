package org.nightlabs.jfire.issuetracking.ui.overview;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.id.UserID;

public class IssueSearchCompositePeopleRelated
extends JDOQueryComposite<Issue, IssueQuery>
{
	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueSearchCompositePeopleRelated(AbstractQueryFilterComposite<Issue, IssueQuery> parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}
	
	public IssueSearchCompositePeopleRelated(
			AbstractQueryFilterComposite<Issue, IssueQuery> filterComposite,
			int style) {
		super(filterComposite, style);
		createComposite(this);
	}

	private static final Logger logger = Logger.getLogger(IssueSearchCompositeIssueRelated.class);
	
	private boolean selectedAllReporter = false;
	private boolean selectedAllAssignee = false;

	private User selectedReporter;
	private User selectedAssignee;

	private Text reporterText;
	private Text assigneeText;

	private Button allReporterButton;
	private Button reporterButton;
	private Button allAssigneeButton;
	private Button assigneeButton;

	@Override
	protected void createComposite(Composite parent) {
		Group userGroup = new Group(parent, SWT.NONE);
		userGroup.setText("People Related");
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		userGroup.setLayout(gridLayout);
		userGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label rLabel = new Label(userGroup, SWT.NONE);
		rLabel.setText("Reporter: ");
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		rLabel.setLayoutData(gridData);

		XComposite rComposite = new XComposite(userGroup, SWT.NONE);
		rComposite.setLayout(new GridLayout(2, false));

		allReporterButton = new Button(rComposite, SWT.CHECK);
		allReporterButton.setText("All");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		allReporterButton.setLayoutData(gridData);
		allReporterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedAllReporter = allReporterButton.getSelection();
				reporterText.setEnabled(!selectedAllReporter);
				reporterButton.setEnabled(!selectedAllReporter);
			}
		});
		allReporterButton.setSelection(true);

		reporterText = new Text(rComposite, SWT.BORDER);
		reporterText.setEditable(false);
		reporterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reporterButton = new Button(rComposite, SWT.PUSH);
		reporterButton.setText("...");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedReporter == null ? "" : selectedReporter.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
					if (selectedReporter != null)
						reporterText.setText(selectedReporter.getName());
				}//if
			}
		});

		Label aLabel = new Label(userGroup, SWT.NONE);
		aLabel.setText("Assignee: ");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalAlignment = GridData.CENTER;
		aLabel.setLayoutData(gridData);

		XComposite aComposite = new XComposite(userGroup, SWT.NONE);
		aComposite.setLayout(new GridLayout(2, false));

		allAssigneeButton = new Button(aComposite, SWT.CHECK);
		allAssigneeButton.setText("All");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		allAssigneeButton.setLayoutData(gridData);
		allAssigneeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedAllAssignee = allAssigneeButton.getSelection();
				assigneeText.setEnabled(!selectedAllAssignee);
				assigneeButton.setEnabled(!selectedAllAssignee);
			}
		});
		allAssigneeButton.setSelection(true);

		assigneeText = new Text(aComposite, SWT.BORDER);
		assigneeText.setEditable(false);
		assigneeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		assigneeButton = new Button(aComposite, SWT.PUSH);
		assigneeButton.setText("...");

		assigneeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedAssignee == null ? "" : selectedAssignee.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedAssignee = userSearchDialog.getSelectedUser();
					if (selectedAssignee != null)
						assigneeText.setText(selectedAssignee.getName());
				}//if
			}
		});		
	}

	@Override
	protected void resetSearchQueryValues() {
		IssueQuery issueQuery = getQuery();
		
		if (!selectedAllReporter && selectedReporter != null) {
			issueQuery.setReporterID((UserID)JDOHelper.getObjectId(selectedReporter));
		}
		else {
			issueQuery.setReporterID(null);
		}

		if (!selectedAllAssignee && selectedAssignee != null) {
			issueQuery.setAssigneeID((UserID)JDOHelper.getObjectId(selectedAssignee));
		}
		else {
			issueQuery.setAssigneeID(null);
		}
	}

	@Override
	protected void unsetSearchQueryValues() {
		IssueQuery issueQuery = getQuery();
		issueQuery.setAssigneeID(null);
		issueQuery.setReporterID(null);
	}
}