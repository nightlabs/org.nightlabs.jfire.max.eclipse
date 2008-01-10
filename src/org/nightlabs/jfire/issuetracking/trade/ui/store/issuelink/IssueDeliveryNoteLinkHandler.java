/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.store.id.DeliveryNoteID;

/**
 * @author chairatk
 *
 */
public class IssueDeliveryNoteLinkHandler 
implements IssueLinkHandler 
{

	public String getLinkObjectDescription(ObjectID objectID) {
		DeliveryNoteID deliveryNoteID = (DeliveryNoteID) objectID;
		return String.format(
				"Delivery Note  %s",
				(deliveryNoteID == null ? "" : deliveryNoteID.deliveryNoteIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(deliveryNoteID.deliveryNoteID)));
	}

	public Image getLinkObjectImage(ObjectID objectID) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueDeliveryNoteLinkHandler.class, 
				"LinkObject").createImage();
	}

	public void openLinkObject() {
		
	}

}
