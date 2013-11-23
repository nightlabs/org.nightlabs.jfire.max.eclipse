package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.print;

import java.awt.print.PrinterException;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.ui.layout.action.print.PrintReportLayoutUtil;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.transfer.print.IArticleContainerPrinter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerPrinter implements IArticleContainerPrinter {

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.print.IArticleContainerPrinter#printArticleContainer(org.nightlabs.jfire.trade.id.ArticleContainerID)
	 */
	@Override
	public void printArticleContainer(ArticleContainerID articleContainerId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("articleContainerID", articleContainerId); //$NON-NLS-1$
		String reportRegistryItemType = null;

		if (articleContainerId instanceof InvoiceID)
			reportRegistryItemType = ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE;
		else if (articleContainerId instanceof DeliveryNoteID)
			reportRegistryItemType = ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_DELIVERY_NOTE;

		ReportLayoutConfigModule cfMod = ConfigUtil.getUserCfMod(ReportLayoutConfigModule.class,
//				new String[] {FetchPlan.ALL},
				new String[] {ReportLayoutConfigModule.FETCH_GROUP_AVAILABLE_LAYOUTS, FetchPlan.DEFAULT},
				3,
				new NullProgressMonitor());
		ReportRegistryItemID defLayoutID = cfMod.getDefaultAvailEntry(reportRegistryItemType);
		if (defLayoutID == null)
			throw new IllegalStateException("No default ReportLayout was set for the category type "+ReportingTradeConstants.REPORT_REGISTRY_ITEM_TYPE_INVOICE); //$NON-NLS-1$
		try {
			PrintReportLayoutUtil.printReportLayout(
					new RenderReportRequest(
							defLayoutID,
							params
					),
					new NullProgressMonitor()
			);
		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
	}

}
