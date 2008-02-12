package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.changetariff;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditActionDelegate;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditAction;
import org.nightlabs.progress.NullProgressMonitor;

public class ChangeTariffActionDelegate
extends ArticleEditActionDelegate
{
	@Override
	public boolean calculateEnabled(ArticleSelection articleSelection, Set<ArticleSelection> articleSelections)
	{
		// should be enabled only, if there's no finalized offer involved!
		Set<OfferID> offerIDs = new HashSet<OfferID>();
		for (Article article : articleSelection.getSelectedArticles())
			offerIDs.add(article.getOfferID());

		for (Article article : articleSelection.getSelectedArticles()) {
			if (article.isAllocationPending() || article.isAllocationAbandoned() || article.isReleasePending() || article.isReleaseAbandoned())
				return false;
		}

		for (OfferID offerID : offerIDs) {
			Offer offer = OfferDAO.sharedInstance().getOffer(offerID, new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			if (offer.isFinalized())
				return false;
		}

//		// check, if all selected articles have the same tariff
//		Tariff selectedTariff = null;
//		for (Article article : articleSelection.getSelectedArticles()) {
//			if (!(article instanceof TicArticle))
//				return false;
//
//			TicArticle ta = (TicArticle) article;
//			if (selectedTariff == null)
//				selectedTariff = ta.getTariff();
//			else if (!selectedTariff.equals(ta.getTariff()))
//				return false;
//		}

		return true;
	}

	@Override
	public void run(IArticleEditAction articleEditAction, ArticleSelection articleSelection)
	{
		Set<ArticleID> selectedArticleIDs = NLJDOHelper.getObjectIDSet(articleSelection.getSelectedArticles());
		ChangeTariffWizard changeTariffWizard = new ChangeTariffWizard(selectedArticleIDs);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(changeTariffWizard);
		dialog.open();
	}
}
