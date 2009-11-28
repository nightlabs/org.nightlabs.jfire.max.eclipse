package org.nightlabs.jfire.pbx.ui.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.config.PhoneSystemConfigModule;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class PhoneSystemConfigModuleController extends AbstractConfigModuleController {

	public static final String[] FETCH_GROUPS_PHONE_SYSTEM = {
		FetchPlan.DEFAULT,
		PhoneSystem.FETCH_GROUP_NAME,
	};

	private static Set<String> fetchGroups;
	static {
		fetchGroups = new HashSet<String>(AbstractConfigModuleController.getCommonConfigModuleFetchGroups());
		fetchGroups.add(PhoneSystemConfigModule.FETCH_GROUP_PHONE_SYSTEM);
		fetchGroups.addAll(CollectionUtil.array2ArrayList(FETCH_GROUPS_PHONE_SYSTEM));
		fetchGroups = Collections.unmodifiableSet(fetchGroups);
	}

	/**
	 * @param preferencePage
	 */
	public PhoneSystemConfigModuleController(AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	@Override
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return PhoneSystemConfigModule.class;
	}

	@Override
	public Set<String> getConfigModuleFetchGroups() {
		return fetchGroups;
	}
}