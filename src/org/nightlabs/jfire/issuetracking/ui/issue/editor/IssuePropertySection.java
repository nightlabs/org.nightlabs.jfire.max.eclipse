/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssuePropertySection extends AbstractIssueEditorGeneralSection {

	private Label priorityLabel;
	private Label severityLabel;
	private Label resolutionLabel;
	
	private XComboComposite<IssuePriority> issuePriorityCombo;
	private XComboComposite<IssueSeverityType> issueSeverityTypeCombo;
	private XComboComposite<IssueResolution> issueResolutionCombo;
	
	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;
	
	/**
	 * @param section
	 * @param managedForm
	 */
	public IssuePropertySection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText("Properties");
		
		priorityLabel = new Label(getClient(), SWT.WRAP);
		priorityLabel.setText("Priority: ");
		priorityLabel.setLayoutData(new GridData());
		
		severityLabel = new Label(getClient(), SWT.WRAP);
		severityLabel.setText("Severity: ");
		severityLabel.setLayoutData(new GridData());
		
		resolutionLabel = new Label(getClient(), SWT.WRAP);
		resolutionLabel.setText("Resolution: ");
		resolutionLabel.setLayoutData(new GridData());
		
		issuePriorityCombo = new XComboComposite<IssuePriority>(getClient(), SWT.WRAP);
		issuePriorityCombo.setLabelProvider(new IssueLabelProvider());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		issuePriorityCombo.setLayoutData(gd);
		
		issueSeverityTypeCombo = new XComboComposite<IssueSeverityType>(getClient(), SWT.WRAP);
		issueSeverityTypeCombo.setLabelProvider(new IssueLabelProvider());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		issueSeverityTypeCombo.setLayoutData(gd);
		
		issueResolutionCombo = new XComboComposite<IssueResolution>(getClient(), SWT.WRAP);
		issueResolutionCombo.setLabelProvider(new IssueLabelProvider());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		issueResolutionCombo.setLayoutData(gd);
	}
	
	protected void doSetIssue(Issue issue) {
		loadProperties(issue);
		severityLabel.setText(
			String.format(
				"Severity Type: %s", 
				issue.getIssueType().getName().getText())
		);
	}
	
	private void loadProperties(final Issue issue){
		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							issueResolutionCombo.removeAll();
							for (IssueResolution ir : issue.getIssueType().getIssueResolutions()) {
								issueResolutionCombo.addElement(ir);
							}
							issueResolutionCombo.selectElement(issue.getIssueResolution());
							
							issuePriorityCombo.removeAll();
							for (IssuePriority ip : issue.getIssueType().getIssuePriorities()) {
								issuePriorityCombo.addElement(ip);
							}
							issuePriorityCombo.selectElement(issue.getIssuePriority());
							
							issueSeverityTypeCombo.removeAll();
							for (IssueSeverityType it : issue.getIssueType().getIssueSeverityTypes()) {
								issueSeverityTypeCombo.addElement(it);
							}
							issueSeverityTypeCombo.selectElement(issue.getIssueSeverityType());
						}
					});
				}catch (Exception e1) {
					ExceptionHandlerRegistry.asyncHandleException(e1);
					throw new RuntimeException(e1);
				}

				return Status.OK_STATUS;
			} 
		};
		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();
	}

}
