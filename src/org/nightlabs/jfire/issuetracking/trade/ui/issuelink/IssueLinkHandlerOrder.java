/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

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
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.OrderDAO;
import org.nightlabs.jfire.trade.ui.overview.order.action.EditOrderAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerOrder 
extends AbstractIssueLinkHandler<OrderID, Order> 
{
	@Override
	protected Collection<Order> _getLinkedObjects(Set<IssueLink> issueLinks,
			Set<OrderID> linkedObjectIDs, ProgressMonitor monitor) {
		return OrderDAO.sharedInstance().getOrders(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	@Override
	public Image getLinkedObjectImage() {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueLinkHandlerOrder.class, 
				"LinkObject").createImage();
	}

	@Override
	public String getLinkedObjectName(OrderID linkedObjectID) {
		return String.format(
				"Order  %s",
				(linkedObjectID == null ? "" : linkedObjectID.orderIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(linkedObjectID.orderID)));
	}

	@Override
	public void openLinkedObject(OrderID linkedObjectID) {
		EditOrderAction editAction = new EditOrderAction();
		editAction.setArticleContainerID(linkedObjectID);
		editAction.run();			
	}
	
	@Override
	public Order getLinkedObject(OrderID linkedObjectID,
			ProgressMonitor monitor) {
		return OrderDAO.sharedInstance().getOrder(
				linkedObjectID,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
}
