/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.SummedPriceFracmentTypeConfigModule;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class SummedPriceFracmentTypeConfigController 
extends	AbstractConfigModuleController 
{
	private static Set<String> fetchGroups;
	static {
		fetchGroups = new HashSet<String>(AbstractConfigModuleController.getCommonConfigModuleFetchGroups());
		fetchGroups.add(SummedPriceFracmentTypeConfigModule.FETCH_GROUP_SUMMED_PRICE_FRACMENT_TYPE_LIST);
		fetchGroups = Collections.unmodifiableSet(fetchGroups);
	}
	
	public SummedPriceFracmentTypeConfigController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	@Override
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return SummedPriceFracmentTypeConfigModule.class;
	}

	@Override
	public Set<String> getConfigModuleFetchGroups() {
		return fetchGroups;
	}
}
