package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOfferDAO;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOrderDAO;
import org.nightlabs.progress.ProgressMonitor;

public class RecurringContainerEditComposite extends ArticleContainerEditComposite {

	public RecurringContainerEditComposite(Composite parent,
			ArticleContainerID containerID) {
		super(parent, containerID);
	}


		
	@Override	
	protected ArticleContainer retrieveArticleContainer(ArticleContainerID articleContainerID, boolean withArticles, ProgressMonitor monitor)
	{
		if (articleContainerID instanceof OrderID)
			return RecurringOrderDAO.sharedInstance().getRecurringOrder(
					(OrderID) articleContainerID,
					withArticles ? FETCH_GROUPS_ORDER_WITH_ARTICLES : FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor
			);

		if (articleContainerID instanceof OfferID)
			return RecurringOfferDAO.sharedInstance().getRecurringOffer(
					(OfferID) articleContainerID,
					withArticles ? FETCH_GROUPS_OFFER_WITH_ARTICLES : FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor
			);


		throw new IllegalArgumentException("articleContainerID type \"" + articleContainerID.getClass().getName() + "\" unknown"); //$NON-NLS-1$ //$NON-NLS-2$
	}



}
