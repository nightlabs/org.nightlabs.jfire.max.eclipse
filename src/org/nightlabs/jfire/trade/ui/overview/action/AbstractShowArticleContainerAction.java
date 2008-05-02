package org.nightlabs.jfire.trade.ui.overview.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.config.ReportConfigUtil;
import org.nightlabs.jfire.reporting.ui.layout.action.view.AbstractViewReportLayoutAction;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractShowArticleContainerAction
extends AbstractArticleContainerAction
{
	public static final String ID = AbstractShowArticleContainerAction.class.getName();

	public AbstractShowArticleContainerAction()
	{
		super();
		init();
	}
		
//	public AbstractShowArticleContainerAction(OverviewEntryEditor editor)
//	{
//		super(editor);
//		init();
//	}
	
	protected void init() {
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.action.AbstractShowArticleContainerAction.toolTipText")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(),
				AbstractShowArticleContainerAction.class));
	}
	
	protected AbstractViewReportLayoutAction showReportAction = new AbstractViewReportLayoutAction() {
		@Override
		protected String getReportUseCaseID() {
			// Use null to force lookup by reportLayoutType
			return null;
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
		showReportAction.setNextRunParams(params);
		showReportAction.runWithRegistryItemIDs(itemIDs);
	}
	
	/**
	 * Prepare the parameter for the ReportLayout in order to view
	 * the selected {@link ArticleContainer}.
	 * The default implementation puts {@link #getArticleContainerID()}
	 * with the key "articleContainerID" into the map.
	 * Override to customize this behaviour;
	 * 
	 * @param params The params that will be passed to the {@link AbstractViewReportLayoutAction}
	 */
	protected void prepareParams(Map<String, Object> params) {
		params.put("articleContainerID", getArticleContainerID()); //$NON-NLS-1$
//		params.put("articleContainerID", JFireReportingHelper.createDataSetParam(getArticleContainerID())); //$NON-NLS-1$
	}
	
	/**
	 * Returns the Report registry type that should be used to
	 * show the selected {@link ArticleContainer}.
	 * 
	 * @return The Report registry type that should be used to
	 * show the selected {@link ArticleContainer}.
	 */
	protected abstract String getReportRegistryItemType();
}
