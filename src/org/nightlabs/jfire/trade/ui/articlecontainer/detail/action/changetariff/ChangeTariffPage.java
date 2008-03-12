package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.ArticleProvider;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.tariff.TariffList;
import org.nightlabs.jfire.trade.ui.tariff.TariffList.TariffFilter;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

public class ChangeTariffPage
		extends WizardHopPage
{
	private TariffList tariffList;
	private Set<ArticleID> selectedArticleIDs;

	public ChangeTariffPage(Set<ArticleID> selectedArticleIDs)
	{
		this(ChangeTariffPage.class.getName(), selectedArticleIDs);
	}

	public ChangeTariffPage(String pageName,  Set<ArticleID> selectedArticleIDs)
	{
		super(pageName, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff.ChangeTariffPage.title.changeTariff")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff.ChangeTariffPage.desciption.changeTariff")); //$NON-NLS-1$

		assert selectedArticleIDs != null : "selectedArticleIDs != null"; //$NON-NLS-1$
		this.selectedArticleIDs = selectedArticleIDs;
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		tariffList = new TariffList(parent, SWT.NONE, false);
		tariffList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectedTariff = tariffList.getFirstSelectedElement();
				getContainer().updateButtons();
			}
		});

		// find out the articles' currently assigned tariffs and the available tariffs (in their assigned price configs)
		// TODO we need to filter the tariffs according to the CustomerGroup as well - as soon as it defines available tariffs
		Job loadArticlesJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff.ChangeTariffPage.job.loadingArticles")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
					throws Exception
			{
				Collection<? extends Article> articles = ArticleProvider.sharedInstance().getArticles(
						selectedArticleIDs,
						new String[] {
								FetchPlan.DEFAULT,
								Article.FETCH_GROUP_TARIFF,
								Article.FETCH_GROUP_PRODUCT_TYPE,
								ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,
								GridPriceConfig.FETCH_GROUP_TARIFFS
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

				Set<GridPriceConfig> packagePriceConfigs = new HashSet<GridPriceConfig>();
				for (Article article : articles)
					packagePriceConfigs.add((GridPriceConfig) article.getProductType().getPackagePriceConfig());

				// find out, if there is only one single tariff assigned currently (or if there are multiple) and if so, which one.
				Tariff _currentlyAssignedTariff = null;
				for (Article article : articles) {
					if (_currentlyAssignedTariff == null)
						_currentlyAssignedTariff = article.getTariff();
					else if (!_currentlyAssignedTariff.equals(article.getTariff())) {
						_currentlyAssignedTariff = null;
						break;
					}
				}
				// we need the currently assigned tariff to be final (because of async runnable)
				final Tariff currentlyAssignedTariff = _currentlyAssignedTariff;

				// find out the smallest set of TariffIDs that is available in all involved price configs
				Set<TariffID> _availableTariffIDs = null;
				for (GridPriceConfig priceConfig : packagePriceConfigs) {
					if (_availableTariffIDs == null)
						_availableTariffIDs = NLJDOHelper.getObjectIDSet(priceConfig.getTariffs());
					else
						_availableTariffIDs.retainAll(NLJDOHelper.getObjectIDSet(priceConfig.getTariffs()));
				}
				final Set<TariffID> availableTariffIDs = _availableTariffIDs;

				final TariffFilter tariffFilter = new TariffFilter() {
					@Override
					public boolean includeTariff(Tariff tariff)
					{
						TariffID tariffID = (TariffID) JDOHelper.getObjectId(tariff);
						return availableTariffIDs.contains(tariffID);
					}
				};

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						assignedTariff = currentlyAssignedTariff;
						selectedTariff = currentlyAssignedTariff;

						if (currentlyAssignedTariff != null)
							tariffList.setSelectedElements(Collections.singletonList(currentlyAssignedTariff));

						tariffList.loadTariffs(null, tariffFilter);

						getContainer().updateButtons();
					}
				});

				return Status.OK_STATUS;
			}
		};
		loadArticlesJob.setPriority(org.eclipse.core.runtime.jobs.Job.INTERACTIVE);
		loadArticlesJob.schedule();

		return tariffList;
	}

	/**
	 * Will be <code>null</code> if there are multiple tariffs assigned to the different articles or the currently assigned tariff.
	 */
	private Tariff assignedTariff;
	private Tariff selectedTariff;

	@Override
	public boolean isPageComplete()
	{
		return selectedTariff != null && !Util.equals(assignedTariff, selectedTariff);
	}

	public Tariff getSelectedTariff()
	{
		return selectedTariff;
	}
}
