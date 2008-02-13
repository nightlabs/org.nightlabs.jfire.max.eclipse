/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.config;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage;
import org.nightlabs.jfire.reporting.config.ReportLayoutAvailEntry;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportLayoutConfigController extends
		AbstractConfigModuleController {

	/**
	 * @param preferencePage
	 */
	public ReportLayoutConfigController(
			AbstractConfigModulePreferencePage preferencePage) {
		super(preferencePage);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.IConfigModuleController#getConfigModuleClass()
	 */
	public Class getConfigModuleClass() {
		return ReportLayoutConfigModule.class;
	}

	private static final Set<String> REPORT_LAYOUT_FETCH_GROUPS = new HashSet<String>();
	
	@Implement
	public Set<String> getConfigModuleFetchGroups() {
		if (REPORT_LAYOUT_FETCH_GROUPS.isEmpty()) {
			REPORT_LAYOUT_FETCH_GROUPS.addAll(getCommonConfigModuleFetchGroups());
			REPORT_LAYOUT_FETCH_GROUPS.add(ReportLayoutConfigModule.FETCH_GROUP_AVAILABLE_LAYOUTS);
			REPORT_LAYOUT_FETCH_GROUPS.add(ReportLayoutAvailEntry.FETCH_GROUP_AVAILABLE_REPORT_LAYOUT_KEYS);
		}
		return REPORT_LAYOUT_FETCH_GROUPS;
	}

}
