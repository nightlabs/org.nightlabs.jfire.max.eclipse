/**
 * 
 */
package org.nightlabs.jfire.trade.ui.modeofpayment.config;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.accounting.pay.config.ModeOfPaymentConfigModule;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;

/**
 * Simple controller for {@link ModeOfPaymentConfigModule}s.
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class ModeOfPaymentConfigModuleController extends AbstractConfigModuleController {

	/**
	 * @param preferencePage
	 */
	public ModeOfPaymentConfigModuleController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	@Override
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return ModeOfPaymentConfigModule.class;
	}

	private static Set<String> FETCH_GROUPS = new HashSet<String>();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleFetchGroups()
	 */
	@Override
	public Set<String> getConfigModuleFetchGroups() {
		if (FETCH_GROUPS.isEmpty()) {
			FETCH_GROUPS.addAll(getCommonConfigModuleFetchGroups());
			FETCH_GROUPS.add(ModeOfPaymentConfigModule.FETCH_GROUP_MODE_OF_PAYMENT_FLAVOURIDS);
		}
		return FETCH_GROUPS;
	}

}
