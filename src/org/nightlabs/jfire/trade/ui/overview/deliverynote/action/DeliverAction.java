package org.nightlabs.jfire.trade.ui.overview.deliverynote.action;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;


/**
 * @author Fitas. [at] NightLabs [dot] de
 *
 */
public class DeliverAction extends AbstractArticleContainerAction
{
	public static final String ID = DeliverAction.class.getName();

	public DeliverAction()
	{
		super();
		setId(ID);
	}
	
	@Override
	public boolean calculateEnabled() {
		if (!super.calculateEnabled())
			return false;		
		ArticleContainer articleContainer = getArticleContainer(new String[] {
				FetchPlan.DEFAULT,
				DeliveryNote.FETCH_GROUP_CUSTOMER_ID,
				DeliveryNote.FETCH_GROUP_ARTICLES,
				DeliveryNote.FETCH_GROUP_DELIVERY_NOTE_LOCAL,
				Article.FETCH_GROUP_ARTICLE_LOCAL
			});
		if (!(articleContainer instanceof DeliveryNote))
			return true;
		DeliveryNote deliveryNote = (DeliveryNote)articleContainer;
		for (Article article : deliveryNote.getArticles())
			if (!article.getArticleLocal().isDelivered())
				return true;

		return false;
	}
	
	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerID();
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_DELIVERY);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
	
	@Override
	public boolean calculateVisible() {
		return true;
	}
}	