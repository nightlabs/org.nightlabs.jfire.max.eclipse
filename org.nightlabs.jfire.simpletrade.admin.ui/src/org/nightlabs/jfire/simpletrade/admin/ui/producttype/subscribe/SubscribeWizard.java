package org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerRemote;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;

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
	public boolean performFinish()
	{
		final String selectedOrganisationID = organisationSelectionPage.getSelectedOrganisationID().organisationID;
		try {
			getContainer().run(true, false, new IRunnableWithProgress(){
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException
				{
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.subscribe.SubscribeWizard.job.importForReselling.name") , 100); //$NON-NLS-1$
					try {
						SimpleTradeManagerRemote simpleTradeManager = JFireEjb3Factory.getRemoteBean(SimpleTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
						simpleTradeManager.importSimpleProductTypesForReselling(selectedOrganisationID);
					} catch (Exception e) {
						throw new RuntimeException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
