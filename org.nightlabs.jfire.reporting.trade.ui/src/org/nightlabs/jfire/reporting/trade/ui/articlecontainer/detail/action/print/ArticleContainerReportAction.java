/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print;

import org.nightlabs.jfire.reporting.trade.ui.util.ReportTradeUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class ArticleContainerReportAction extends ArticleContainerAction {

	/**
	 * 
	 */
	public ArticleContainerReportAction() {
	}

	/**
	 * @return the correct ReportRegistryItemType for the type of ArticleContainer that's edited
	 */
	protected String getReportRegistryItemType() {
		ArticleContainerID articleContainerID = getArticleContainerID();
		return ReportTradeUtil.getReportRegistryItemType(articleContainerID);
	}
}
