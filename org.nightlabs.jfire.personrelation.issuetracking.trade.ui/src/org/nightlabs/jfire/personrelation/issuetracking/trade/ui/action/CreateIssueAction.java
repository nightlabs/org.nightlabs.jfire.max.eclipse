package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.action;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueWizard;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class CreateIssueAction implements IViewActionDelegate
{
	private IViewPart view;

	@Override
	public void init(IViewPart view) {
		this.view = view;
	}

	@Override
	public void run(IAction action) {
		final PropertySetID selectedPersonID = this.selectedPersonID;
		if (selectedPersonID == null)
			return;

		final Shell shell = view.getSite().getShell();
		final Display display = shell.getDisplay();

		Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.action.CreateIssueAction.job.openingWizard.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.action.CreateIssueAction.task.openingWizard.name"), 100); //$NON-NLS-1$
				try {
					final CreateIssueWizard wizard = new CreateIssueWizard(selectedPersonID, new SubProgressMonitor(monitor, 100));

					display.asyncExec(new Runnable() {
						public void run() {
							DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(shell, wizard);
							dialog.open();
						}
					});

					return Status.OK_STATUS;
				} finally {
					monitor.done();
				}
			}
		};
		job.setUser(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	private PropertySetID selectedPersonID = null;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedPersonID = null;
		PersonRelationTreeNode node = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(selection);
		if (node == null) {
			action.setEnabled(false);
			return;
		}

		selectedPersonID = node.getPropertySetID();
		action.setEnabled(selectedPersonID != null);
	}
}
