package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOrderDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.ArticleContainerRootTreeNode;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringOrderRootTreeNode extends ArticleContainerRootTreeNode {

	public static final String[] FETCH_GROUPS_ORDER = new String[] {
		FetchPlan.DEFAULT,
		Order.FETCH_GROUP_CURRENCY,
		Order.FETCH_GROUP_CUSTOMER_ID,
		Order.FETCH_GROUP_VENDOR_ID
	};

	public RecurringOrderRootTreeNode(HeaderTreeNode parent, boolean purchaseMode, boolean endCustomerMode)
	{
		super(
				parent,
				"Orders", //$NON-NLS-1$
				parent.getHeaderTreeComposite().getImageOrderRootTreeNode(),
				purchaseMode,
				endCustomerMode
		);
		init();
	}

	@Override
	protected HeaderTreeNode createArticleContainerNode(byte position,
			ArticleContainer articleContainer) {
		return new RecurringOrderTreeNode(this, position, (RecurringOrder) articleContainer);
	}

	@Override
	protected List<Object> doLoadChildElements(AnchorID vendorID,
			AnchorID customerID, long rangeBeginIdx, long rangeEndIdx,
			ProgressMonitor monitor
	) throws Exception
	{
		AnchorID endCustomerID = null;
		if (isEndCustomerMode()) {
			endCustomerID = customerID;
			customerID = null;
		}

		return CollectionUtil.castList(
				RecurringOrderDAO.sharedInstance().getRecurringOrders(
						vendorID, customerID, endCustomerID,
						rangeBeginIdx, rangeEndIdx,
						FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				)
		);
	}

	@Override
	protected List<ArticleContainer> doLoadNewArticleContainers(
			Set<ArticleContainerID> articleContainerIDs, ProgressMonitor monitor) {
		Set<OrderID> orderIDs = CollectionUtil.castSet(articleContainerIDs);
		return CollectionUtil.castList(RecurringOrderDAO.sharedInstance().getRecurringOrders(
				orderIDs,
				FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor)
		);
	}

	@Override
	protected boolean acceptNewArticleContainer(Object newObjectID) {
		Class<?> objectClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(newObjectID);
		return RecurringOrder.class.isAssignableFrom(objectClass);
	}
}
