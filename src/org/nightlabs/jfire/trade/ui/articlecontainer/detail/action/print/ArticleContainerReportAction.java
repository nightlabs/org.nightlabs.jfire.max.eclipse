/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.print;

import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
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
		
		if (articleContainerID instanceof InvoiceID)
			return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE;
		else if (articleContainerID instanceof OrderID)
			return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_ORDER;
		else if (articleContainerID instanceof OfferID)
			return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_OFFER;
		else if (articleContainerID instanceof DeliveryNoteID)
			return ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;
		return null;
	}
}
