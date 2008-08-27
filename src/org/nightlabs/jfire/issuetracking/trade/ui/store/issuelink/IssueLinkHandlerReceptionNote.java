/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.store.dao.ReceptionNoteDAO;
import org.nightlabs.jfire.store.id.ReceptionNoteID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerReceptionNote 
extends AbstractIssueLinkHandler<ReceptionNoteID, ReceptionNote>
{
	@Override
	protected Collection<ReceptionNote> _getLinkedObjects(Set<IssueLink> issueLinks, Set<ReceptionNoteID> linkedObjectIDs, ProgressMonitor monitor) {
		Collection<ReceptionNote> receptionNoteSet = null;
		try {
			receptionNoteSet = ReceptionNoteDAO.sharedInstance().getReceptionNotes(
					linkedObjectIDs, 
					new String[] { FetchPlan.DEFAULT }, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return receptionNoteSet;
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, ReceptionNote linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueLinkHandlerReceptionNote.class, 
				"LinkedObject").createImage();
	}

	@Override
	public String getLinkedObjectName(IssueLink issueLink, ReceptionNote linkedObject) {
//		return String.format(
//				"Reception Note  %s",
//				linkedObject.getPrimaryKey());
		throw new UnsupportedOperationException("NYI");
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, ReceptionNoteID linkedObjectID) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NYI");
	}

}
