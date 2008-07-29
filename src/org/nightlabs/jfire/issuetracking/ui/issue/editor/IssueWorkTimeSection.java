package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueWorkTimeSection 
extends AbstractIssueEditorGeneralSection 
{
	private Issue issue;
	
	private Label startTimeLabel;
	private Button startStopButton;
	
	
	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueWorkTimeSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText("Work Time");
		
		new Label(getClient(), SWT.NONE).setText("Status: ");
				
		startTimeLabel = new Label(getClient(), SWT.NONE);
		startTimeLabel.setText(" - ");
		
		startStopButton = new Button(getClient(), SWT.NONE);
		startStopButton.setText("");
		startStopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				if (issue.isStarted()) {
					issue.endWorking(new Date());
				}
				
				else {
					if (issue.getAssignee() == null) {
						UserSearchDialog userSearchDialog = new UserSearchDialog(getSection().getShell(), null);
						int returnCode = userSearchDialog.open();
						if (returnCode == Dialog.OK) {
							issue.setAssignee(userSearchDialog.getSelectedUser());
						}
					}
					issue.startWorking(new Date());
				}
				markDirty();
			}
		});
		
		new Label(getClient(), SWT.NONE).setText("Time: ");
		Label finishTimeLabel = new Label(getClient(), SWT.NONE);
		finishTimeLabel.setText("");
	}

	@Override
	protected void doSetIssue(Issue newIssue) {
		this.issue = newIssue;
		
		if (issue.isStarted()) 
			startStopButton.setText("Stop");
		else 
			startStopButton.setText("Start");
	}
}
