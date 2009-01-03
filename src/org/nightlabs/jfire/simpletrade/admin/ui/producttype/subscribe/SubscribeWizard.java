package org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe;

import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;

public class SubscribeWizard
		extends DynamicPathWizard
{
	private OrganisationSelectionPage organisationSelectionPage;

	@Override
	public void addPages()
	{
		organisationSelectionPage = new OrganisationSelectionPage();
		addPage(organisationSelectionPage);
	}

	@Override
	@Implement
	public boolean performFinish()
	{
		String selectedOrganisationID = organisationSelectionPage.getSelectedOrganisationID().organisationID;
		try {
			SimpleTradeManager simpleTradeManager = JFireEjbFactory.getBean(SimpleTradeManager.class, Login.getLogin().getInitialContextProperties());
			simpleTradeManager.importSimpleProductTypesForReselling(selectedOrganisationID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
