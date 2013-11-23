/**
 * 
 */
package org.nightlabs.jfire.trade.ui.modeofdelivery.config;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.store.deliver.config.ModeOfDeliveryConfigModule;

/**
 * Simple controller for {@link ModeOfDeliveryConfigModule}s.
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class ModeOfDeliveryConfigModuleController extends AbstractConfigModuleController {

	/**
	 * @param preferencePage
	 */
	public ModeOfDeliveryConfigModuleController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	@Override
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return ModeOfDeliveryConfigModule.class;
	}

	private static Set<String> FETCH_GROUPS = new HashSet<String>();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleFetchGroups()
	 */
	@Override
	public Set<String> getConfigModuleFetchGroups() {
		if (FETCH_GROUPS.isEmpty()) {
			FETCH_GROUPS.addAll(getCommonConfigModuleFetchGroups());
			FETCH_GROUPS.add(ModeOfDeliveryConfigModule.FETCH_GROUP_MODE_OF_DELIVERY_FLAVOURIDS);
		}
		return FETCH_GROUPS;
	}

}
