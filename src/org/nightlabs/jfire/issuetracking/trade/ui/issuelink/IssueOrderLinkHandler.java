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
import org.nightlabs.jfire.trade.id.OrderID;

/**
 * @author chairatk
 *
 */
public class IssueOrderLinkHandler 
implements IssueLinkHandler 
{
	public String getLinkObjectDescription(ObjectID objectID) {
		OrderID orderID = (OrderID)objectID;
		
		return String.format(
				"Order  %s",
				(orderID == null ? "" : orderID.orderIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(orderID.orderID)));
	}

	public Image getLinkObjectImage(ObjectID objectID) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueOrderLinkHandler.class, 
				"LinkObject").createImage();
	}

	public void openLinkObject() {
		
	}


}
