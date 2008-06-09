package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.security.User;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateIssueDetailWizardPage 
extends WizardHopPage
{
	//GUI
	private Label linkedObjectLbl;
	private IssueLinkAdderComposite issueLinkAdderComposite;
	
	private Label reporterLbl;
	private Text reporterText;
	private Button reporterButton;

	private Label assigntoUserLbl;
	private Text assignToUserText;
	private Button assignToUserButton;
	
	private Label fileLabel;
	private IssueFileAttachmentComposite fileComposite;

	//Used objects
	private User selectedReporter;
	private User selectedAssignToUser;
	
	private Issue issue;

	public CreateIssueDetailWizardPage(Issue issue) {
		super(CreateIssueDetailWizardPage.class.getName(), "New Issue", SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), CreateIssueWizardPage.class));
		setDescription("Create a new issue.");
		this.issue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		linkedObjectLbl = new Label(mainComposite, SWT.NONE);
		linkedObjectLbl.setText("Linked object");
		
		issueLinkAdderComposite = new IssueLinkAdderComposite(mainComposite, SWT.NONE, true, issue);
		
		/**********Reporter**********/
		reporterLbl = new Label(mainComposite, SWT.NONE);
		reporterLbl.setText("Reporter: ");

		XComposite reporterComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		reporterComposite.getGridLayout().numColumns = 2;

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		reporterComposite.setLayoutData(gridData);

		reporterText = new Text(reporterComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		reporterText.setLayoutData(gridData);
		selectedReporter = Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		issue.setReporter(selectedReporter);
		reporterText.setText(selectedReporter.getName());

		reporterButton = new Button(reporterComposite, SWT.PUSH);
		reporterButton.setText("Choose User");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedReporter == null ? "" : selectedReporter.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
					reporterText.setText(selectedReporter == null ? "" : selectedReporter.getName());
					issue.setReporter(selectedReporter);
				}//if
			}
		});
		/*******************************/
		/**********Assigned To**********/
		assigntoUserLbl = new Label(mainComposite, SWT.NONE);
		assigntoUserLbl.setText("Assigned To: ");

		XComposite toUserComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		toUserComposite.getGridLayout().numColumns = 2;

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		toUserComposite.setLayoutData(gridData);

		assignToUserText = new Text(toUserComposite, SWT.BORDER | SWT.READ_ONLY);
		gridData  = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		assignToUserText.setLayoutData(gridData);

		assignToUserButton = new Button(toUserComposite, SWT.PUSH);
		assignToUserButton.setText("Choose User");

		assignToUserButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedAssignToUser == null ? "" : selectedAssignToUser.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedAssignToUser = userSearchDialog.getSelectedUser();
					assignToUserText.setText(selectedAssignToUser == null ? "" : selectedAssignToUser.getName());
					issue.setAssignee(selectedAssignToUser);
				}//if
			}
		});

		fileLabel = new Label(mainComposite, SWT.NONE);
		fileLabel.setText("Files: ");

		fileComposite = new IssueFileAttachmentComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER, issue);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumHeight = 80;
		fileComposite.setLayoutData(gridData);
		
		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}
	
	public User getSelectedReporter() {
		return selectedReporter;
	}

	public User getSelectedAssignToUser() {
		return selectedAssignToUser;
	}

	public IssueLinkAdderComposite getIssueLinkAdderComposite() {
		return issueLinkAdderComposite;
	}
}
