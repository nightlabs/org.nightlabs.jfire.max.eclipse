package org.nightlabs.jfire.trade.ui.legalentity.search.config;

import org.nightlabs.jfire.base.ui.prop.search.config.AbstractPropertySetSearchPreferencePage;
import org.nightlabs.jfire.layout.AbstractEditLayoutConfigModule;
import org.nightlabs.jfire.prop.config.PropertySetEditLayoutConfigModule;
import org.nightlabs.jfire.trade.config.LegalEntitySearchConfigModule;
import org.nightlabs.jfire.trade.ui.legalentity.search.PersonSearchUseCaseConstants;

/**
 * PropertySetSearchPreferencePage for the use-case {@link PersonSearchUseCaseConstants#USE_CASE_ID_LEGALENTITY_SEARCH}
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class LegalEntitySearchPreferencePage extends AbstractPropertySetSearchPreferencePage {

	@Override
	public String getConfigModuleID() {
		return AbstractEditLayoutConfigModule.getCfModID(AbstractEditLayoutConfigModule.CLIENT_TYPE_RCP,
				PersonSearchUseCaseConstants.USE_CASE_ID_LEGALENTITY_SEARCH);
	}
	
	@Override
	protected Class<? extends PropertySetEditLayoutConfigModule> getConfigModuleClass() {
		return LegalEntitySearchConfigModule.class;
	}
	
	@Override
	public String getUseCaseDescription() {
		return "You can configure the search for legal entities here, which is used in the trade perspective.";
	}
	
	@Override
	protected String getLayoutConfigTabText() {
		return "Search configuration";
	}
}