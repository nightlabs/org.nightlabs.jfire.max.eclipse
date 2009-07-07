package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;

public class CreateOrLinkIssueAction implements IViewActionDelegate
{
	private IViewPart view;

	@Override
	public void init(IViewPart view) {
		this.view = view;
	}

	@Override
	public void run(IAction action)
	{
		final Display display = view.getSite().getShell().getDisplay();
		final PropertySetID selectedPersonID = this.selectedPersonID;
		if (selectedPersonID == null)
			return;

		Job job = new Job("Opening wizard") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask("Opening wizard", 100);
				try {
					final Person person = (Person) PropertySetDAO.sharedInstance().getPropertySet(
							selectedPersonID, null, 1, monitor
					);

					display.asyncExec(new Runnable() {
						public void run() {
							AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(person);
							DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(attachIssueToObjectWizard)
							{
								@Override
								protected Point getInitialSize()
								{
									return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
								}
							};
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

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel.size() != 1 || sel.getFirstElement() == null) {
			action.setEnabled(false);
			return;
		}

		Object object = sel.getFirstElement();
		if (!(object instanceof PersonRelationTreeNode)) {
			action.setEnabled(false);
			return;
		}

		PersonRelationTreeNode node = (PersonRelationTreeNode) object;
		if (node.getJdoObjectID() instanceof PropertySetID)
			selectedPersonID = (PropertySetID) node.getJdoObjectID();
		else if (node.getJdoObject() instanceof PersonRelation) {
			PersonRelation pr = (PersonRelation) node.getJdoObject();
			selectedPersonID = pr.getToID();
		}
		action.setEnabled(selectedPersonID != null);
	}
}
