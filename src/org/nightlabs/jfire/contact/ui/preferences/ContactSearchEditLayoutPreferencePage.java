package org.nightlabs.jfire.contact.ui.preferences;

import org.nightlabs.jfire.base.ui.prop.search.config.AbstractPersonSearchEditLayoutPreferencePage;
import org.nightlabs.jfire.contact.ui.PersonSearchUseCaseConstants;
import org.nightlabs.jfire.layout.AbstractEditLayoutConfigModule;

public class ContactSearchEditLayoutPreferencePage extends AbstractPersonSearchEditLayoutPreferencePage {

	@Override
	protected String getConfigModuleID() {
		return AbstractEditLayoutConfigModule.getCfModID(AbstractEditLayoutConfigModule.CLIENT_TYPE_RCP, PersonSearchUseCaseConstants.USE_CASE_ID_CONTACT_SEARCH);
	}

	@Override
	public String getUseCaseDescription() {
		return "You can configure the layout of the search for contacts here.";
	}
}
