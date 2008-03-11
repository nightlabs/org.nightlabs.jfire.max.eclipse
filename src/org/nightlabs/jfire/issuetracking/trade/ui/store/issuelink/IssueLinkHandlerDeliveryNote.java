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
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.articlecontainer.DeliveryNoteDAO;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.EditDeliveryNoteAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerDeliveryNote 
extends AbstractIssueLinkHandler<DeliveryNoteID, DeliveryNote>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, DeliveryNote linkedObject) {
		DeliveryNoteID deliveryNoteID = (DeliveryNoteID) issueLink.getLinkedObjectID();
		return String.format(
				"Delivery Note  %s",
				(issueLink.getLinkedObjectID() == null ? "" : deliveryNoteID.deliveryNoteIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(deliveryNoteID.deliveryNoteID)));
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, DeliveryNote linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueLinkHandlerDeliveryNote.class, 
				"LinkObject").createImage();
	}


	@Override
	public void openLinkedObject(IssueLink issueLink, DeliveryNoteID objectID) {
		EditDeliveryNoteAction editAction = new EditDeliveryNoteAction();
		editAction.setArticleContainerID(objectID);
		editAction.run();
	}

	@Override
	protected Collection<DeliveryNote> _getLinkedObjects(
			Set<IssueLink> issueLinks, Set<DeliveryNoteID> linkedObjectIDs,
			ProgressMonitor monitor)
	{
		return DeliveryNoteDAO.sharedInstance().getDeliveryNotes(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
}
