/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.overview.action.EditIssueAction;

/**
 * @author chairatk
 *
 */
public class IssueIssueLinkHandler 
implements IssueLinkHandler 
{
	public String getLinkObjectDescription(ObjectID objectID) {
		IssueID issueID = (IssueID)objectID;
		return String.format(
				"Issue %s",
				(issueID == null ? "" : issueID.issueID));
		
	}

	public Image getLinkObjectImage(ObjectID objectID) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				IssueIssueLinkHandler.class, 
				"LinkObject").createImage();
	}

	public void openLinkObject(ObjectID objectID) {
		EditIssueAction editAction = new EditIssueAction();
		Collection<IssueID> ids = new ArrayList<IssueID>();
		ids.add((IssueID)objectID);
		editAction.setSelectedIssueIDs(ids);
		editAction.run();	
	}
}