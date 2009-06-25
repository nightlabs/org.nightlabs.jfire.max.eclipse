package org.nightlabs.jfire.reporting.trade.ui.config;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.reporting.trade.config.TradeDocumentsLocaleConfigModule;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TradeDocumentsLocaleConfigController extends
		AbstractConfigModuleController {

	/**
	 * @param preferencePage
	 */
	public TradeDocumentsLocaleConfigController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	public Class<TradeDocumentsLocaleConfigModule> getConfigModuleClass() {
		return TradeDocumentsLocaleConfigModule.class;
	}

	@Override
	public TradeDocumentsLocaleConfigModule getConfigModule() {
		return (TradeDocumentsLocaleConfigModule) super.getConfigModule();
	}
	
	private static final Set<String> CF_MOD_FETCH_GROUPS = new HashSet<String>();

	@Override
	public Set<String> getConfigModuleFetchGroups() {
		if (CF_MOD_FETCH_GROUPS.isEmpty()) {
			CF_MOD_FETCH_GROUPS.addAll(getCommonConfigModuleFetchGroups());
		}
		return CF_MOD_FETCH_GROUPS;
	}

}
