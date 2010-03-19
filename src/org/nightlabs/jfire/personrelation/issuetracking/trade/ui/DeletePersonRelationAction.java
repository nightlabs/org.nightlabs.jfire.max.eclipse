package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationTypeID;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.progress.ProgressMonitor;

public class DeletePersonRelationAction implements IViewActionDelegate
{
	@Override
	public void init(IViewPart view) { }

	@Override
	public void run(IAction action) {
		final PersonRelation personRelation = this.selectedPersonRelation;
		if (personRelation == null)
			return;

		Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.DeletePersonRelationAction.job.deletingPersonRelation.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				PersonRelationTypeID personRelationTypeID = (PersonRelationTypeID) JDOHelper.getObjectId(personRelation.getPersonRelationType());
				if (personRelationTypeID == null)
					throw new IllegalStateException("JDOHelper.getObjectId(personRelation.getPersonRelationType()) returned null! personRelation=" + personRelation + " personRelationType=" + personRelation.getPersonRelationType()); //$NON-NLS-1$ //$NON-NLS-2$

				PersonRelationDAO.sharedInstance().deletePersonRelation(
						personRelationTypeID,
						personRelation.getFromID(),
						personRelation.getToID(),
						monitor
				);

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	private PersonRelation selectedPersonRelation = null;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedPersonRelation = null;
		PersonRelationTreeNode node = PersonRelationTreeNode.getPersonRelationTreeNodeFromSelection(selection);
		if (node == null) {
			action.setEnabled(false);
			return;
		}

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

		if (node.getJdoObject() instanceof PersonRelation) {
			PersonRelation pr = (PersonRelation) node.getJdoObject();
			selectedPersonRelation = pr;
		}
		action.setEnabled(selectedPersonRelation != null);
	}
}
