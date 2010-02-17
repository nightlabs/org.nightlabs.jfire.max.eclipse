/**
 * 
 */
package org.nightlabs.jfire.trade.ui.currency.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.TradeConfigModule;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class CurrencyConfigController 
extends	AbstractConfigModuleController 
{
	public static final String[] FETCH_GROUPS_CURRENCY = {
		FetchPlan.DEFAULT
	};
	
	private static Set<String> fetchGroups;
	static {
		fetchGroups = new HashSet<String>(AbstractConfigModuleController.getCommonConfigModuleFetchGroups());
		fetchGroups.add(TradeConfigModule.FETCH_GROUP_CURRENCY);
		fetchGroups.addAll(CollectionUtil.array2ArrayList(FETCH_GROUPS_CURRENCY));
		fetchGroups = Collections.unmodifiableSet(fetchGroups);
	}
	
	public CurrencyConfigController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	@Override
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return TradeConfigModule.class;
	}

	@Override
	public Set<String> getConfigModuleFetchGroups() {
		return fetchGroups;
	}
}