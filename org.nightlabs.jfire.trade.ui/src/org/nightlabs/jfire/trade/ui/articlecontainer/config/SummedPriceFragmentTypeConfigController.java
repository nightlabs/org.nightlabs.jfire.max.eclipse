/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.SummedPriceFragmentTypeConfigModule;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class SummedPriceFragmentTypeConfigController 
extends	AbstractConfigModuleController 
{
	public static final String[] FETCH_GROUPS_PRICE_FRAGMENT_TYPE = {
		FetchPlan.DEFAULT,
		PriceFragmentType.FETCH_GROUP_NAME,
	};
	
	private static Set<String> fetchGroups;
	static {
		fetchGroups = new HashSet<String>(AbstractConfigModuleController.getCommonConfigModuleFetchGroups());
		fetchGroups.add(SummedPriceFragmentTypeConfigModule.FETCH_GROUP_SUMMED_PRICE_FRAGMENT_TYPE_LIST);
		fetchGroups.addAll(CollectionUtil.array2ArrayList(FETCH_GROUPS_PRICE_FRAGMENT_TYPE));
		fetchGroups = Collections.unmodifiableSet(fetchGroups);
	}
	
	public SummedPriceFragmentTypeConfigController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	@Override
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return SummedPriceFragmentTypeConfigModule.class;
	}

	@Override
	public Set<String> getConfigModuleFetchGroups() {
		return fetchGroups;
	}
}
