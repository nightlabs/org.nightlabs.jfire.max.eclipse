package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print;

import java.util.Locale;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.progress.NullProgressMonitor;

public class ArticleContainerReportActionHelper {

	/**
	 * Retrieves the given {@link ArticleContainer}s customer and its persons Locale.
	 * @param reportID TODO
	 * @param params TODO
	 * @return The locale of the given {@link ArticleContainer}s customer.
	 */
	public static Locale getArticleContainerReportLocale(ArticleContainerID articleContainerID, ReportRegistryItemID reportID, Map<String, Object> params) {
		// TODO: Query config-module for checked option (customer-locale?, user-locale?, always ask?)
		if (articleContainerID != null) { 
			ArticleContainer articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
					articleContainerID, 
					new String[] {FetchPlan.DEFAULT, ArticleContainer.FETCH_GROUP_CUSTOMER, LegalEntity.FETCH_GROUP_PERSON}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			if (articleContainer.getCustomer() != null) {
				return articleContainer.getCustomer().getPerson().getLocale();
			}
		}
		return null;
	}

}
