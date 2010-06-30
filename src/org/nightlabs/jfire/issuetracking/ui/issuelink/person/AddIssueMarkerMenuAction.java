package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import java.io.ByteArrayInputStream;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 *
 * An action that addes the Issue Markers to the Issue link.
 *
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class AddIssueMarkerMenuAction extends Action {

	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_MARKERS
	};

	public AddIssueMarkerMenuAction() {
		super();
	}

	private IIssueLinkSelection issueLinkSelection;
	private IssueMarker issueMarker;

	public void init(IIssueLinkSelection issueLinkSelection, IssueMarker issueMarker) {
		this.issueLinkSelection = issueLinkSelection;
		this.issueMarker = issueMarker;
	}

	@Override
	public void run() {
		if (issueLinkSelection == null && issueLinkSelection.getSelectedIssueLink() == null)
			return;

		final IssueLink issueLink = issueLinkSelection.getSelectedIssueLink();
		final Issue issue = issueLink.getIssue();

		Job job = new Job(Messages.getString(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddIssueMarkerMenuAction.job.addRemoveIssueMarker"))) {  //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddIssueMarkerMenuAction.job.addRemoveIssueMarker"), 100);		 //$NON-NLS-1$
				// reverse state of the GUI CheckBox
				if(isChecked())
					issue.addIssueMarker(issueMarker);
				else
					issue.removeIssueMarker(issueMarker);

				monitor.worked(30);
				IssueDAO.sharedInstance().storeIssue(issue,false,FETCH_GROUP_ISSUE,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 70));
				monitor.done();
				return Status.OK_STATUS;
			}
		};

		job.setPriority(Job.SHORT);
		job.schedule();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		byte[] iconByte = issueMarker.getIcon16x16Data();
		if (iconByte != null) {
			ByteArrayInputStream in = new ByteArrayInputStream( iconByte );
			Image icon = new Image(Display.getCurrent(), in);
			return ImageDescriptor.createFromImage(icon);
		}
		return null;
	}

	@Override
	public String getText() {
		return issueMarker.getName().getText();
	}

	@Override
	public String getToolTipText() {
		return issueMarker.getDescription().getText();
	}
}
