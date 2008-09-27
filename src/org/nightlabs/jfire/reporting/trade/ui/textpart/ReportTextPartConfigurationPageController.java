/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.textpart;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.reporting.config.ReportLayoutAvailEntry;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;
import org.nightlabs.jfire.reporting.textpart.dao.ReportTextPartConfigurationDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.report.ReportTradeUtil;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationPageController extends EntityEditorPageController {

	private static final String[] FETCH_GROUPS_CONFIG = new String[] {
		FetchPlan.DEFAULT, ReportTextPartConfiguration.FETCH_GROUP_REPORT_TEXT_PARTS,
		ReportTextPart.FETCH_GROUP_NAME, ReportTextPart.FETCH_GROUP_CONTENT
	};
	
	private Map<ReportRegistryItemID, ReportTextPartConfiguration> configurations = new HashMap<ReportRegistryItemID, ReportTextPartConfiguration>();
	private Set<ReportTextPartConfiguration> dirtyConfigurations = new HashSet<ReportTextPartConfiguration>(); 
	
	private ArticleContainerID articleContainerID;
	
	private Collection<ReportRegistryItem> reportRegistryItems;
	private ReportRegistryItem defaultReportRegistryItem; 
	
	/**
	 * @param editor
	 */
	public ReportTextPartConfigurationPageController(EntityEditor editor) {
		super(editor);
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ReportTextPartConfigurationPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.IEntityEditorPageController#doLoad(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask("Loading text part prerequisites", 10);
		ReportLayoutConfigModule cfMod = (ReportLayoutConfigModule)ConfigUtil.getUserCfMod(
				ReportLayoutConfigModule.class,
				new String[] {FetchPlan.DEFAULT, ReportLayoutConfigModule.FETCH_GROUP_AVAILABLE_LAYOUTS, ReportLayoutAvailEntry.FETCH_GROUP_AVAILABLE_REPORT_LAYOUT_KEYS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, 5)
			);
		String reportRegistryItemType = ReportTradeUtil.getReportRegistryItemType(articleContainerID);
		Collection<ReportRegistryItemID> allItems = cfMod.getAvailEntries(reportRegistryItemType);
		reportRegistryItems = ReportRegistryItemDAO.sharedInstance().getReportRegistryItems(
				new HashSet<ReportRegistryItemID>(allItems), 
				new String[] {FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_NAME}, new SubProgressMonitor(monitor, 3));
		ReportRegistryItemID defID = cfMod.getDefaultAvailEntry(reportRegistryItemType);
		if (defID != null) {
			defaultReportRegistryItem = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(
				defID, 
				new String[] {FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_NAME}, new SubProgressMonitor(monitor, 2));
		}
		fireModifyEvent(null, null);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.IEntityEditorPageController#doSave(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public boolean doSave(ProgressMonitor monitor) {
		monitor.beginTask("Store ReporTextPartConfiguration", dirtyConfigurations.size() * 5 + 1);
		monitor.worked(1);
		try {
			for (Map.Entry<ReportRegistryItemID, ReportTextPartConfiguration> configs : configurations.entrySet()) {
				if (dirtyConfigurations.contains(configs.getValue())) {
					ReportTextPartConfiguration storedConfig = ReportTextPartConfigurationDAO.sharedInstance().storeLinkedObjectReportTextPartConfiguration(
							configs.getValue(), true, FETCH_GROUPS_CONFIG, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 5));
					dirtyConfigurations.remove(configs.getValue());
					configurations.put(configs.getKey(), storedConfig);
				}
			}
			fireModifyEvent(null, null);
		} finally {
			monitor.done();
		}
		return dirtyConfigurations.size() == 0;
	}

	public ReportTextPartConfiguration getReportTextPartConfiguration(ReportRegistryItemID reportRegistryItemID, ProgressMonitor monitor) {
		monitor.beginTask("Loading ReporTextPartConfiguration", 10);
		try {
			if (configurations.containsKey(reportRegistryItemID))
				return configurations.get(reportRegistryItemID);
			
			monitor.worked(5);
			ReportTextPartConfiguration configuration = ReportTextPartConfigurationDAO.sharedInstance().getReportTextPartConfiguration(
					reportRegistryItemID, articleContainerID, true, 
					FETCH_GROUPS_CONFIG, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					new SubProgressMonitor(monitor, 5));
			configurations.put(reportRegistryItemID, configuration);
			
			return configuration;
		} finally {
			monitor.done();
		}
	}
	
	@Override
	public void markUndirty() {
		dirtyConfigurations.clear();
		super.markUndirty();
	}
	
	public void markDirty(ReportTextPartConfiguration reportTextPartConfiguration) {
		if (dirtyConfigurations.add(reportTextPartConfiguration)) {
			super.markDirty();
		}
	}
	
	public Collection<ReportRegistryItem> getReportRegistryItems() {
		return reportRegistryItems;
	}
	
	public ReportRegistryItem getDefaultReportRegistryItem() {
		return defaultReportRegistryItem;
	}
}
