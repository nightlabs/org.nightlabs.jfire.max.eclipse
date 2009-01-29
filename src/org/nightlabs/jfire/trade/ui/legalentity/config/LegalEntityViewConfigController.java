/**
 * 
 */
package org.nightlabs.jfire.trade.ui.legalentity.config;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.LegalEntityViewConfigModule;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntityViewConfigController extends
		AbstractConfigModuleController {

	/**
	 * @param preferencePage
	 */
	public LegalEntityViewConfigController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return LegalEntityViewConfigModule.class;
	}

	private static Set<String> LEGAL_ENTITY_VIEW_FETCH_GROUPS = new HashSet<String>();
	
	@Override
	public Set<String> getConfigModuleFetchGroups() {
		if (LEGAL_ENTITY_VIEW_FETCH_GROUPS.isEmpty()) {
			LEGAL_ENTITY_VIEW_FETCH_GROUPS.addAll(getCommonConfigModuleFetchGroups());
			LEGAL_ENTITY_VIEW_FETCH_GROUPS.add(LegalEntityViewConfigModule.FETCH_GROUP_PERSONSTRUCTFIELDS);
		}
		return LEGAL_ENTITY_VIEW_FETCH_GROUPS;
	}
}
