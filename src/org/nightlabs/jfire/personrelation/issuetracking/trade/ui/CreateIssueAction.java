package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

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
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
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

		Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueAction.job.openingWizard.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueAction.task.openingWizard.name"), 100); //$NON-NLS-1$
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

//		selectedPersonID = null;
//
//		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
//			action.setEnabled(false);
//			return;
//		}
//
//		IStructuredSelection sel = (IStructuredSelection) selection;
//		if (sel.size() != 1 || sel.getFirstElement() == null) {
//			action.setEnabled(false);
//			return;
//		}
//
//		Object object = sel.getFirstElement();
//		if (!(object instanceof PersonRelationTreeNode)) {
//			action.setEnabled(false);
//			return;
//		}
//
//		PersonRelationTreeNode node = (PersonRelationTreeNode) object;
//		while (selectedPersonID == null && node != null) {
//			if (node.getJdoObjectID() instanceof PropertySetID) {
//				selectedPersonID = (PropertySetID) node.getJdoObjectID();
//				break;
//			}
//			else if (node.getJdoObject() instanceof PersonRelation) {
//				PersonRelation pr = (PersonRelation) node.getJdoObject();
//				selectedPersonID = pr.getToID();
//				break;
//			}
////			else if (node.getJdoObjectID() instanceof IssueLinkID) {
////				node = (PersonRelationTreeNode) node.getParent();
////			}
//			else
//				break;
//		}
//		action.setEnabled(selectedPersonID != null);
	}
}
