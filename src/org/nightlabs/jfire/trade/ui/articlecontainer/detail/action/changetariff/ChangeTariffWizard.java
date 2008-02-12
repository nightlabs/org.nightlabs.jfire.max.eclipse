package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff;

import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.progress.ProgressMonitor;

public class ChangeTariffWizard
		extends DynamicPathWizard
{
	private Set<ArticleID> selectedArticleIDs;
	private ChangeTariffPage changeTariffPage;

	public ChangeTariffWizard(Set<ArticleID> selectedArticleIDs)
	{
		assert selectedArticleIDs != null : "selectedArticleIDs != null";
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

		Job setSelectedTariffJob = new Job("Set tariff") {
			@Override
			protected IStatus run(ProgressMonitor monitor)
					throws Exception
			{
				TradeManager tm = TradeManagerUtil.getHome(org.nightlabs.jfire.base.ui.login.Login.getLogin().getInitialContextProperties()).create();
				tm.assignTariff(selectedArticleIDs, selectedTariffID, false, null, 1);
				return Status.OK_STATUS;
			}
		};
		setSelectedTariffJob.setPriority(Job.INTERACTIVE);
		setSelectedTariffJob.setUser(true);
		setSelectedTariffJob.schedule();

		return true;
	}

}
