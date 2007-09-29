/**
 * 
 */
package org.nightlabs.jfire.trade.admin.ui.deliveryqueue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DeliveryQueueConfigModuleController extends AbstractConfigModuleController {
	
	private static Set<String> fetchGroups;	
	static {
		fetchGroups = new HashSet<String>(AbstractConfigModuleController.getCommonConfigModuleFetchGroups());
		fetchGroups.add(DeliveryQueueConfigModule.FETCH_GROUP_VISIBLE_DELIVERY_QUEUES);
		fetchGroups = Collections.unmodifiableSet(fetchGroups);
	}

	/**
	 * @param preferencePage
	 */
	public DeliveryQueueConfigModuleController(AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	public Class getConfigModuleClass() {
		return DeliveryQueueConfigModule.class;
	}

	@Implement
	public Set<String> getConfigModuleFetchGroups() {
		return fetchGroups;
	}
}
