package org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.organisation.OrganisationIDDataSource;
import org.nightlabs.jfire.base.ui.organisation.OrganisationList;
import org.nightlabs.jfire.organisation.id.OrganisationID;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;

public class OrganisationSelectionPage
		extends WizardHopPage
{
	private OrganisationList organisationList;

	public OrganisationSelectionPage()
	{
		super(OrganisationSelectionPage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe.OrganisationSelectionPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe.OrganisationSelectionPage.description")); //$NON-NLS-1$
	}

	@Override
	@Implement
	public Control createPageContents(Composite parent)
	{
		organisationList = new OrganisationList(parent, new OrganisationIDDataSource() {
			public Collection<OrganisationID> getOrganisationIDs()
			{
				try {
					SimpleTradeManager m = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					return m.getCandidateOrganisationIDsForCrossTrade();
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		});
		organisationList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectedOrganisationID = (OrganisationID) JDOHelper.getObjectId(organisationList.getSelectedElement());
				getContainer().updateButtons();
			}
		});
		return organisationList;
	}

	private OrganisationID selectedOrganisationID = null;

	@Override
	public boolean isPageComplete()
	{
		return selectedOrganisationID != null;
	}

	public OrganisationID getSelectedOrganisationID()
	{
		return selectedOrganisationID;
	}
}
