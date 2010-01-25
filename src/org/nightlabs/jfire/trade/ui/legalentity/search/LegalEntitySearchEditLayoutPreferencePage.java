package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.nightlabs.jfire.base.ui.prop.search.config.AbstractPersonSearchEditLayoutPreferencePage;
import org.nightlabs.jfire.layout.AbstractEditLayoutConfigModule;

public class LegalEntitySearchEditLayoutPreferencePage extends AbstractPersonSearchEditLayoutPreferencePage {

	@Override
	protected String getConfigModuleID() {
		return AbstractEditLayoutConfigModule.getCfModID(AbstractEditLayoutConfigModule.CLIENT_TYPE_RCP, PersonSearchUseCaseConstants.USE_CASE_ID_LEGALENTITY_SEARCH);
	}

	@Override
	public String getUseCaseDescription() {
		return "You can configure the layout of the search for legal entities here.";
	}
}
