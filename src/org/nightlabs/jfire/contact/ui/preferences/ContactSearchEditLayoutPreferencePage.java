package org.nightlabs.jfire.contact.ui.preferences;

import org.nightlabs.jfire.base.ui.prop.search.config.AbstractPropertySetSearchPreferencePage;
import org.nightlabs.jfire.contact.ui.PersonSearchUseCaseConstants;
import org.nightlabs.jfire.layout.AbstractEditLayoutConfigModule;
import org.nightlabs.jfire.person.PersonSearchConfigModule;
import org.nightlabs.jfire.prop.config.PropertySetEditLayoutConfigModule;

/**
 * PropertySetSearchPreferencePage for the use-case {@link PersonSearchUseCaseConstants#USE_CASE_ID_CONTACT_SEARCH}
 *  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ContactSearchEditLayoutPreferencePage extends AbstractPropertySetSearchPreferencePage {

	@Override
	public String getConfigModuleID() {
		return AbstractEditLayoutConfigModule.getCfModID(AbstractEditLayoutConfigModule.CLIENT_TYPE_RCP, PersonSearchUseCaseConstants.USE_CASE_ID_CONTACT_SEARCH);
	}

	@Override
	protected Class<? extends PropertySetEditLayoutConfigModule> getConfigModuleClass() {
		return PersonSearchConfigModule.class;
	}
	
	@Override
	public String getUseCaseDescription() {
		return "You can configure the layout of the search for contacts here.";
	}

	@Override
	protected String getLayoutConfigTabText() {
		return "Search configuration";
	}
}
