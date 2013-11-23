package org.nightlabs.jfire.asterisk.ui.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.config.AsteriskConfigModule;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class AsteriskServerConfigModuleController extends AbstractConfigModuleController {

	public static final String[] FETCH_GROUPS_ASTERISK_SERVER = {
		FetchPlan.DEFAULT,
		AsteriskServer.FETCH_GROUP_CALL_FILE_PROPERTIES,
	};

	private static Set<String> fetchGroups;
	static {
		fetchGroups = new HashSet<String>(AbstractConfigModuleController.getCommonConfigModuleFetchGroups());
		fetchGroups.add(AsteriskConfigModule.FETCH_GROUP_CALL_FILE_PROPERTIES);
		fetchGroups.add(AsteriskConfigModule.FETCH_GROUP_CALL_FILE_OVERRIDE_KEYS);
		fetchGroups.addAll(CollectionUtil.array2ArrayList(FETCH_GROUPS_ASTERISK_SERVER));
		fetchGroups = Collections.unmodifiableSet(fetchGroups);
	}

	/**
	 * @param preferencePage
	 */
	public AsteriskServerConfigModuleController(AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	public Class<? extends ConfigModule> getConfigModuleClass() {
		return AsteriskConfigModule.class;
	}

	@Override
	public Set<String> getConfigModuleFetchGroups() {
		return fetchGroups;
	}
}