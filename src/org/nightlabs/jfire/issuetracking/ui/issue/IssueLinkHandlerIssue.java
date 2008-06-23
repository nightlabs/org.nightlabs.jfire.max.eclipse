/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.overview.action.EditIssueAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueLinkHandlerIssue 
extends AbstractIssueLinkHandler<IssueID, Issue>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, Issue linkedObject) {
		return String.format(
				"Issue %s",
				linkedObject.getPrimaryKey()); // TODO there must be the subject and maybe some other data be shown
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, Issue linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				IssueLinkHandlerIssue.class, 
		"LinkedObject").createImage();
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, IssueID linkedObjectID) {
		EditIssueAction editAction = new EditIssueAction();
		Collection<IssueID> ids = new ArrayList<IssueID>();
		ids.add(linkedObjectID);
		editAction.setSelectedIssueIDs(ids);
		editAction.run();	
	}

	@Override
	protected Collection<Issue> _getLinkedObjects(
			Set<IssueLink> issueLinks, Set<IssueID> linkedObjectIDs,
			ProgressMonitor monitor)
			{
		return IssueDAO.sharedInstance().getIssues(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
			}
}