/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.overview.offer.action.EditOfferAction;

/**
 * @author chairatk
 *
 */
public class IssueOfferLinkHandler 
implements IssueLinkHandler 
{
	public String getLinkObjectDescription(ObjectID objectID) {
		OfferID offerID = (OfferID)objectID;
		return String.format(
				"Offer %s",
				(offerID == null ? "" : offerID.offerIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(offerID.offerID)));
		
	}

	public Image getLinkObjectImage(ObjectID objectID) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueOfferLinkHandler.class, 
				"LinkObject").createImage();
	}

	public void openLinkObject(ObjectID objectID) {
		EditOfferAction editAction = new EditOfferAction();
		editAction.setArticleContainerID(objectID);
		editAction.run();	
	}
}
