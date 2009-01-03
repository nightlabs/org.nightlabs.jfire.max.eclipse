package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff;

import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class ChangeTariffWizard
		extends DynamicPathWizard
{
	private Set<ArticleID> selectedArticleIDs;
	private ChangeTariffPage changeTariffPage;

	public ChangeTariffWizard(Set<ArticleID> selectedArticleIDs)
	{
		assert selectedArticleIDs != null : "selectedArticleIDs != null"; //$NON-NLS-1$
		this.selectedArticleIDs = selectedArticleIDs;
	}

	@Override
	public void addPages()
	{
		changeTariffPage = new ChangeTariffPage(selectedArticleIDs);
		addPage(changeTariffPage);
	}

	@Override
	public boolean performFinish()
	{
		final TariffID selectedTariffID = (TariffID) JDOHelper.getObjectId(changeTariffPage.getSelectedTariff());

		Job setSelectedTariffJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff.ChangeTariffWizard.job.setTariff")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
					throws Exception
			{
				TradeManager tm = JFireEjbFactory.getBean(TradeManager.class, org.nightlabs.jfire.base.ui.login.Login.getLogin().getInitialContextProperties());
				tm.assignTariff(selectedArticleIDs, selectedTariffID, false, null, 1);
				return Status.OK_STATUS;
			}
		};
		setSelectedTariffJob.setPriority(org.eclipse.core.runtime.jobs.Job.INTERACTIVE);
		setSelectedTariffJob.setUser(true);
		setSelectedTariffJob.schedule();

		return true;
	}

}
