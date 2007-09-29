package org.nightlabs.jfire.trade.overview.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.reporting.config.ReportConfigUtil;
import org.nightlabs.jfire.reporting.layout.action.print.AbstractPrintReportLayoutAction;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.trade.TradePlugin;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractPrintArticleContainerAction 
extends AbstractArticleContainerAction 
{
	public static final String ID = AbstractPrintArticleContainerAction.class.getName();

	public AbstractPrintArticleContainerAction() {
		super();
		init();
	}
	
//	public AbstractPrintArticleContainerAction(OverviewEntryEditor editor) {
//		super(editor);
//		init();
//	}

	protected void init() {
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.overview.action.AbstractPrintArticleContainerAction.text")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				TradePlugin.getDefault(), AbstractPrintArticleContainerAction.class));		
	}
	
	protected AbstractPrintReportLayoutAction printReportAction = new AbstractPrintReportLayoutAction() {
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
		printReportAction.setNextRunParams(params);
		printReportAction.runWithRegistryItemIDs(itemIDs);
	}	
	
	protected abstract void prepareParams(Map<String, Object> params);
	protected abstract String getReportRegistryItemType();	
}
