/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import javax.jdo.FetchPlan;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author abieber
 *
 */
public class ArticleContainerEditorUtil {

	public static void openArticleContainerInTradePespective(ArticleContainerID articleContainerID) {
		IEditorInput input = new ArticleContainerEditorInput(articleContainerID);
		String articleContainerEditorID = ArticleContainerEditor.ID_EDITOR;
		
		openArticleContainerInTradePespective(input, articleContainerEditorID);
		
	}

	public static void openArticleContainerInTradePespective(IEditorInput input, String articleContainerEditorID) {
		try {
			Editor2PerspectiveRegistry.sharedInstance().openEditor(input, articleContainerEditorID);
	
			ArticleContainerEditorInput articleContainerEditorInput = (ArticleContainerEditorInput) input;
	
			String[] fetchGroups  = new String[] {
				FetchPlan.DEFAULT,
				Order.FETCH_GROUP_CUSTOMER_ID,
				Offer.FETCH_GROUP_CUSTOMER_ID,
				DeliveryNote.FETCH_GROUP_CUSTOMER_ID,
				Invoice.FETCH_GROUP_CUSTOMER_ID,
				ReceptionNote.FETCH_GROUP_CUSTOMER_ID
			};
			
			ArticleContainer articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
					articleContainerEditorInput.getArticleContainerID(), fetchGroups,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
	
			// TODO: maybe rewrite Editor2PerspectiveRegistry to automatically send an event to the perspective
			// 		with the new input so that all elements that need to be updated if an editor is updated can
			// 		listen for that event. => This would unify the handling of new editors in a perspective.
			NotificationEvent event = new NotificationEvent(articleContainer, TradePlugin.ZONE_SALE, articleContainer.getCustomerID(), LegalEntity.class);
			SelectionManager.sharedInstance().notify(event);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
