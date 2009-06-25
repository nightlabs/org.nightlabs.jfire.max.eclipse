package org.nightlabs.jfire.reporting.trade.ui.overview.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.trade.ui.JFireReportingTradePlugin;
import org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.ArticleContainerReportActionHelper;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.config.ReportConfigUtil;
import org.nightlabs.jfire.reporting.ui.layout.action.print.AbstractPrintReportLayoutAction;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Abstract action that prints an '{@link ArticleContainer}-report' of a certain category,
 * i.e. an offer-layout, invoice-layout etc.
 * The category of the the report has to be provided by the implementation class (see {@link #getReportRegistryItemType()}).
 * <p>
 * The only parameter of the rendered reports is assumed to have the name "articleContainerID"
 * and be of type {@link OrderID}, {@link OfferID}, {@link InvoiceID} etc.
 * If you want to override this behaviour, override {@link #prepareParams(Map)}.
 * </p>
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class AbstractPrintArticleContainerAction
extends AbstractArticleContainerAction
{
	public static final String ID = AbstractPrintArticleContainerAction.class.getName();
	public static final String PARAMETER_ID_ARTICLE_CONTAINER_ID = "articleContainerID"; //$NON-NLS-1$

	public AbstractPrintArticleContainerAction() {
		super();
		init();
	}

	protected void init() {
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.overview.action.AbstractPrintArticleContainerAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				JFireReportingTradePlugin.getDefault(), AbstractPrintArticleContainerAction.class));
	}

	protected AbstractPrintReportLayoutAction printReportAction = new AbstractPrintReportLayoutAction() {
		@Override
		protected Locale getRenderRequestLocale(ReportRegistryItemID reportID, Map<String, Object> params, ProgressMonitor monitor) {
			return ArticleContainerReportActionHelper.getArticleContainerReportLocale(getArticleContainerID(), reportID, params, monitor);
		}
	};

	@Override
	public void run()
	{
		Map <String, Object> params = new HashMap<String,Object>();
		prepareParams(params);
		ReportRegistryItemID selectedLayoutID = ReportConfigUtil.getReportLayoutID(getReportRegistryItemType());
		if (selectedLayoutID == null) {
			// the user canceled, abort
			return;
		}
		Set<ReportRegistryItemID> itemIDs = new HashSet<ReportRegistryItemID>();
		itemIDs.add(selectedLayoutID);
		printReportAction.setNextRunParams(params);
		printReportAction.runWithRegistryItemIDs(itemIDs);
	}

	/**
	 * Prepare the parameter for the ReportLayout in order to print
	 * the selected {@link ArticleContainer}.
	 * The default implementation puts {@link #getArticleContainerID()}
	 * with the key "articleContainerID" into the map.
	 * Override to customize this behaviour;
	 *
	 * @param params The params that will be passed to the {@link AbstractPrintReportLayoutAction}
	 */
	protected void prepareParams(Map<String, Object> params) {
		params.put(PARAMETER_ID_ARTICLE_CONTAINER_ID, getArticleContainerID());
	}

	/**
	 * Returns the report registry type that should be used to
	 * show the selected {@link ArticleContainer}.
	 *
	 * @return The report registry type that should be used to
	 * show the selected {@link ArticleContainer}.
	 */
	protected abstract String getReportRegistryItemType();
}
