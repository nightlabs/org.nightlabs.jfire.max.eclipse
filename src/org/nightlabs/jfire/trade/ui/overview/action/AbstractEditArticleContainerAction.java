package org.nightlabs.jfire.trade.ui.overview.action;

import javax.jdo.FetchPlan;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.overview.action.IOverviewEditAction;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractEditArticleContainerAction
extends AbstractArticleContainerAction
implements IOverviewEditAction
{
	public AbstractEditArticleContainerAction() {
		super();
	}

	public static String[] FETCH_GROUPS  = new String[] {
		FetchPlan.DEFAULT,
		Order.FETCH_GROUP_CUSTOMER_ID,
		Offer.FETCH_GROUP_CUSTOMER_ID,
		DeliveryNote.FETCH_GROUP_CUSTOMER_ID,
		Invoice.FETCH_GROUP_CUSTOMER_ID,
		ReceptionNote.FETCH_GROUP_CUSTOMER_ID
	};
	
	@Override
	public void run()
	{
		try {
			IEditorInput input = getEditorInput();
			if (! (input instanceof ArticleContainerEditorInput) )
				throw new IllegalArgumentException("This subclass: "+this+" does not return an input type, which is not a subclass of ArticleContainerEditorInput. This must not be allowed!"); //$NON-NLS-1$ //$NON-NLS-2$

			Editor2PerspectiveRegistry.sharedInstance().openEditor(getEditorInput(), getEditorID());
			
			ArticleContainerEditorInput articleContainerEditorInput = (ArticleContainerEditorInput) input;
			
			ArticleContainer articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
					articleContainerEditorInput.getArticleContainerID(), FETCH_GROUPS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			
			// TODO: Which object should be set as source?
			// TODO: maybe rewrite Editor2PerspectiveRegistry to automatically send an event to the perspective
			// 		with the new input so that all elements that need to be updated if an editor is updated can
			// 		listen for that event. => This would unify the handling of new editors in a perspective.
			NotificationEvent event = new NotificationEvent(this, TradePlugin.ZONE_SALE, articleContainer.getCustomerID(), LegalEntity.class);
			SelectionManager.sharedInstance().notify(event);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
