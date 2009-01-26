/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

import java.util.Collection;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.EditDeliveryNoteAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 */
public class IssueLinkHandlerDeliveryNote 
extends AbstractIssueLinkHandler<DeliveryNoteID, DeliveryNote>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, DeliveryNote deliveryNote) {
		DeliveryNoteID deliveryNoteID = (DeliveryNoteID) issueLink.getLinkedObjectID();
		return String.format(
				"Delivery Note  %s", //$NON-NLS-1$
				deliveryNoteID.organisationID + '/' + deliveryNoteID.deliveryNoteIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(deliveryNoteID.deliveryNoteID));
//				deliveryNote.getPrimaryKey());
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, DeliveryNote deliveryNote) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueLinkHandlerDeliveryNote.class, 
				"LinkedObject").createImage(); //$NON-NLS-1$
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, DeliveryNoteID deliveryNoteID) {
		EditDeliveryNoteAction editAction = new EditDeliveryNoteAction();
		editAction.setArticleContainerID(deliveryNoteID);
		editAction.run();
	}

	@Override
	protected Collection<DeliveryNote> _getLinkedObjects(Set<IssueLink> issueLinks, Set<DeliveryNoteID> deliveryNoteIDs, ProgressMonitor monitor)
	{
		return null;
//		return DeliveryNoteDAO.sharedInstance().getDeliveryNotes(
//				deliveryNoteIDs,
//				new String[] { FetchPlan.DEFAULT },
//				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//				monitor);
	}
}
