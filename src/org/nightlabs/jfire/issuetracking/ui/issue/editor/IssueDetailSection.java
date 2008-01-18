/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.security.User;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueDetailSection extends AbstractIssueEditorGeneralSection {

	private Label reporterLabel;
	private Label reporterTextLabel;

	private Label assigneeLabel;
	private Label assigneeTextLabel;

	private Label createdTimeLabel;
	private Label createdTimeTextLabel;

	private Label updatedTimeLabel;
	private Label updatedTimeTextLabel;
	
	private User assigneeUser;
	
	private Issue issue;

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueDetailSection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 2;
		getSection().setText("General Details");

		reporterLabel = new Label(getClient(), SWT.WRAP);
		reporterLabel.setText("Reporter: ");

		reporterTextLabel = new Label(getClient(), SWT.NONE);
		reporterTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		assigneeLabel = new Label(getClient(), SWT.WRAP);
		assigneeLabel.setText("Assignee: ");

		assigneeTextLabel = new Label(getClient(), SWT.NONE);
		assigneeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createdTimeLabel = new Label(getClient(), SWT.WRAP);
		createdTimeLabel.setText("Created Time: ");

		createdTimeTextLabel = new Label(getClient(), SWT.NONE);
		createdTimeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		updatedTimeLabel = new Label(getClient(), SWT.WRAP);
		updatedTimeLabel.setText("Updated Time: ");

		updatedTimeTextLabel = new Label(getClient(), SWT.NONE);
		updatedTimeTextLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		PrintIssueAction printAction = new PrintIssueAction();
		PrintPreviewIssueAction printPreviewAction = new PrintPreviewIssueAction();
		ReassignAction reassignAction = new ReassignAction();

		getToolBarManager().add(reassignAction);
		getToolBarManager().add(printAction);
		getToolBarManager().add(printPreviewAction);

		updateToolBarManager();
	}

	protected void doSetIssue(Issue issue) {
		this.issue = issue;
		
		if (issue.getReporter() != null)
			reporterTextLabel.setText(issue.getReporter().getName());
		else
			reporterTextLabel.setText("");
		
		if (issue.getAssignee() != null)
			assigneeTextLabel.setText(issue.getAssignee().getName());
		else
			assigneeTextLabel.setText("");

		createdTimeTextLabel.setText(issue.getCreateTimestamp().toString());
		updatedTimeTextLabel.setText(issue.getUpdateTimestamp() == null? "-" : issue.getUpdateTimestamp().toString());
	}

	public class ReassignAction extends Action {		
		public ReassignAction() {
			super();
			setId(ReassignAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueDetailSection.class, 
			"Reassign"));
			setToolTipText("Reassign");
			setText("ReassignAction");
		}

		@Override
		public void run() {
			UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
			int returnCode = userSearchDialog.open();
			if (returnCode == Dialog.OK) {
				assigneeUser = userSearchDialog.getSelectedUser();
				if (assigneeUser != null) {
					issue.setAssignee(assigneeUser);
					assigneeTextLabel.setText(issue.getAssignee().getName());
					markDirty();
				}
			}//if
		}		
	}

	class PrintIssueAction extends Action {		
		public PrintIssueAction() {
			super();
			setId(PrintIssueAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueDetailSection.class, 
			"Print"));
			setToolTipText("Print");
			setText("Print");
		}

		@Override
		public void run() {
		}		
	}

	class PrintPreviewIssueAction extends Action {		
		public PrintPreviewIssueAction() {
			super();
			setId(PrintPreviewIssueAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueDetailSection.class, 
					"PrintPreview"));
			setToolTipText("Print Preview");
			setText("Print Preview");
		}

		@Override
		public void run() {
		}		
	}
}
