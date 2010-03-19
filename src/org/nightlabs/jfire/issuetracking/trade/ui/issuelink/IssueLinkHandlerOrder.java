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
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.OrderDAO;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.overview.order.action.EditOrderAction;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerOrder
extends AbstractIssueLinkHandler<OrderID, Order>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, Order order) {
		return String.format(
				Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.IssueLinkHandlerOrder.order.name"), //$NON-NLS-1$
//				linkedObject.getPrimaryKey());
				ArticleContainerUtil.getArticleContainerID(order),
				DateFormatter.formatDateShort(order.getCreateDT(), false));
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, Order linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(),
				IssueLinkHandlerOrder.class,
				"LinkedObject").createImage(); //$NON-NLS-1$
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, OrderID linkedObjectID) {
		EditOrderAction editAction = new EditOrderAction();
		editAction.setArticleContainerID(linkedObjectID);
		editAction.run();
	}

	@Override
	protected Collection<Order> _getLinkedObjects(Set<IssueLink> issueLinks,
			Set<OrderID> linkedObjectIDs, ProgressMonitor monitor) {
		return OrderDAO.sharedInstance().getOrders(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
}
