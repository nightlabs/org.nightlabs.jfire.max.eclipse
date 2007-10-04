package org.nightlabs.jfire.simpletrade.admin.producttype.subscribe;

import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;

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

	@Implement
	public boolean performFinish()
	{
		String selectedOrganisationID = organisationSelectionPage.getSelectedOrganisationID().organisationID;
		try {
			SimpleTradeManager simpleTradeManager = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			simpleTradeManager.importSimpleProductTypesForReselling(selectedOrganisationID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
