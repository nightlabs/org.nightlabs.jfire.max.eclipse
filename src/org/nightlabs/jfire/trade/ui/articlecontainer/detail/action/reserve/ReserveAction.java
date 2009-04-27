/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reserve;

import javax.jdo.JDOHelper;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author daniel
 *
 */
public class ReserveAction extends ArticleContainerAction {

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateVisible()
	 */
	@Override
	public boolean calculateVisible() {
		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		if (!super.calculateEnabled())
			return false;

		ArticleContainer articleContainer = getArticleContainer();
		if (!(articleContainer instanceof Order) || articleContainer.getArticleCount() < 1)
			return false;

		Order order = (Order) articleContainer;

		boolean hasNonFinalizedOffer = false;
		for (Offer offer : order.getOffers()) {
			if (!offer.isFinalized()) {
				hasNonFinalizedOffer = true;
				break;
			}
		}
		return hasNonFinalizedOffer;
	}

	@Override
	public void run()
	{
		// TODO get quickSearch text from quickSaleEditor
		LegalEntity legalEntity = LegalEntitySearchCreateWizard.open("", true); //$NON-NLS-1$
		if (legalEntity != null) {
			Order order = (Order) getArticleContainer();
			try {
				TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
				AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
				tm.createReservation((OrderID) JDOHelper.getObjectId(order), customerID);

				// close editor for the reservation
				IEditorReference[] editorReferences = RCPUtil.getActiveWorkbenchPage().getEditorReferences();
				for (IEditorReference editorReference : editorReferences) {
					IEditorInput input = editorReference.getEditorInput();
					if (input instanceof ArticleContainerEditorInput) {
						ArticleContainerEditorInput editorInput = (ArticleContainerEditorInput) input;
						if (editorInput.getArticleContainerID().equals(getArticleContainerID())) {
							RCPUtil.closeEditor(input, false);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
