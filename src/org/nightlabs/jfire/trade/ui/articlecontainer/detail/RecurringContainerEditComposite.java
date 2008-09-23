package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.OfferLocal;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.SegmentType;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOfferDAO;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOrderDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOrderHeaderComposite;
import org.nightlabs.progress.ProgressMonitor;

public class RecurringContainerEditComposite extends ArticleContainerEditComposite {


	public RecurringContainerEditComposite(Composite parent,
			ArticleContainerID containerID) {
		super(parent, containerID);
	}

	public static final String[] FETCH_GROUPS_RECURRING_ARTICLE_CONTAINER_WITHOUT_ARTICLES = {
		FetchPlan.DEFAULT,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		StatableLocal.FETCH_GROUP_STATE
	};

	public static final String[] FETCH_GROUPS_RECURRING_ORDER_WITH_ARTICLES = {
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		Order.FETCH_GROUP_THIS_ORDER, Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_RECURRING_OFFER_WITH_ARTICLES = {
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		RecurringOffer.FETCH_GROUP_ARTICLES,
		RecurringOffer.FETCH_GROUP_RECURRING_OFFER_CONFIGURATION,
		OfferLocal.FETCH_GROUP_THIS_OFFER_LOCAL,
		StatableLocal.FETCH_GROUP_STATE, Order.FETCH_GROUP_CUSTOMER_GROUP,
		Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR, FetchPlan.DEFAULT };


	@Override	
	protected ArticleContainer retrieveArticleContainer(ArticleContainerID articleContainerID, boolean withArticles, ProgressMonitor monitor)
	{
		if (articleContainerID instanceof OrderID)
			return RecurringOrderDAO.sharedInstance().getRecurringOrder(
					(OrderID) articleContainerID,
					withArticles ? FETCH_GROUPS_RECURRING_ORDER_WITH_ARTICLES : FETCH_GROUPS_RECURRING_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor
			);

		if (articleContainerID instanceof OfferID)
			return RecurringOfferDAO.sharedInstance().getRecurringOffer(
					(OfferID) articleContainerID,
					withArticles ? FETCH_GROUPS_RECURRING_OFFER_WITH_ARTICLES : FETCH_GROUPS_RECURRING_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor
			);


		throw new IllegalArgumentException("articleContainerID type \"" + articleContainerID.getClass().getName() + "\" unknown"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override		
	protected HeaderComposite createHeaderComposite(Composite parent) {

		if (getArticleContainer() instanceof RecurringOrder)
			return new RecurringOrderHeaderComposite(this, (RecurringOrder) getArticleContainer());

		if(getArticleContainer() instanceof RecurringOffer)
			return new RecurringOfferHeaderComposite(this, (RecurringOffer) getArticleContainer());

		throw new IllegalStateException("The current ArticleContainer is of an unsupported type: " + //$NON-NLS-1$
				(getArticleContainer() != null ? getArticleContainer().getClass().getName() : "null") + "."); //$NON-NLS-1$
	}





}
