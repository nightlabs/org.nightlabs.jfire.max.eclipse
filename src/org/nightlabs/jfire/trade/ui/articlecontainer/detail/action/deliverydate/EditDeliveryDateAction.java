package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.dao.DeliveryNoteDAO;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.DeliveryDateMode;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GenericArticleEditAction;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditDeliveryDateAction
extends GenericArticleEditAction
{
	@Override
	public boolean calculateEnabled(Set<ArticleSelection> articleSelections)
	{
		// TODO check why this method must be called, because otherwise getArticles() in run() is null
		super.calculateEnabled(articleSelections);

		ArticleContainer ac = getArticleContainer();
		// check if ArticleContainer is Offer or Order
		if (ac instanceof Offer || ac instanceof Order) {
			// should be enabled only, if there's no finalized offer involved!
			Set<OfferID> offerIDs = new HashSet<OfferID>();
			for (ArticleSelection articleSelection : articleSelections)
			{
				for (Article article : articleSelection.getSelectedArticles())
					offerIDs.add(article.getOfferID());
			}

			Collection<Offer> offers = OfferDAO.sharedInstance().getOffers(offerIDs, new String[] { FetchPlan.DEFAULT },
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			for (Offer offer : offers) {
				if (offer.isFinalized())
					return false;
			}

			return true;
		}
		// check if ArticleContainer is DeliveryNote
		else if (ac instanceof DeliveryNote) {
			// should be enabled only, if there's no finalized deliveryNote involved!
			Set<DeliveryNoteID> deliveryNoteIDs = new HashSet<DeliveryNoteID>();
			for (ArticleSelection articleSelection : articleSelections)
			{
				for (Article article : articleSelection.getSelectedArticles())
					deliveryNoteIDs.add(article.getDeliveryNoteID());
			}

			Collection<DeliveryNote> deliveryNotes = DeliveryNoteDAO.sharedInstance().getDeliveryNotes(deliveryNoteIDs,
					new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			for (DeliveryNote deliveryNote : deliveryNotes) {
				if (deliveryNote.isFinalized())
					return false;
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean calculateVisible()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GenericArticleEditAction#excludeArticle(org.nightlabs.jfire.trade.Article)
	 */
	@Override
	protected boolean excludeArticle(Article article)
	{
		return false;
	}

	@Override
	public void run()
	{
		Shell shell = getArticleContainerEdit().getComposite().getShell();
		EditDeliveryDateDialog dialog = new EditDeliveryDateDialog(shell, getArticles(), getMode());
		dialog.open();
	}

	private DeliveryDateMode getMode()
	{
		if (getArticleContainer() instanceof DeliveryNote) {
			return DeliveryDateMode.DELIVERY_NOTE;
		}
		else {
			return DeliveryDateMode.OFFER;
		}
	}

}
