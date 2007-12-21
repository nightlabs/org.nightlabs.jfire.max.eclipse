/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.jfire.issue.Issue;

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
	}
	
	protected void doSetIssue(Issue issue) {
		reporterTextLabel.setText(issue.getReporter().getName());
		assigneeTextLabel.setText(issue.getAssignee().getName());
		
		createdTimeTextLabel.setText(issue.getCreateTimestamp().toString());
		updatedTimeTextLabel.setText(issue.getUpdateTimestamp() == null? "-" : issue.getUpdateTimestamp().toString());
	}

}
