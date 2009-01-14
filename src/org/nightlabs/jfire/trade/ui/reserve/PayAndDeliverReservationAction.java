/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class PayAndDeliverReservationAction
//extends SelectionAction
extends AbstractArticleContainerAction
{
	public static final String ID = PayAndDeliverReservationAction.class.getName();

	public PayAndDeliverReservationAction()
	{
		super();
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.PayAndDeliverReservationAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.PayAndDeliverReservationAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), PayAndDeliverReservationAction.class));
	}

	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerID();
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				articleContainerID,
				AbstractCombiTransferWizard.TRANSFER_MODE_BOTH);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}

//	@Override
//	public boolean calculateEnabled()
//	{
//		boolean enabled = super.calculateEnabled();
//		if (enabled) {
//			return !rejected;
//		}
//		return false;
//	}
//
//	private boolean rejected = false;
//
//	@Override
//	public void setArticleContainerID(ObjectID objectID) {
//		super.setArticleContainerID(objectID);
//		if (objectID instanceof OfferID) {
//			OfferID offerID = (OfferID) objectID;
//			Offer offer = OfferDAO.sharedInstance().getOffer(offerID, new String[] {FetchPlan.DEFAULT, Offer.FETCH_GROUP_OFFER_LOCAL},
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//			rejected = offer.getOfferLocal().isRejected();
//		}
//	}
}
