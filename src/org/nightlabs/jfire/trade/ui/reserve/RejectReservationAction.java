/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.jbpm.JbpmConstantsOffer;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class RejectReservationAction
extends AbstractArticleContainerAction
{
	public static final String ID = RejectReservationAction.class.getName();

	public RejectReservationAction() {
		super();
		setId(ID);
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.RejectReservationAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.RejectReservationAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), RejectReservationAction.class));
	}

	@Override
	public void run()
	{
		ArticleContainerID articleContainerID = getArticleContainerID();
		if (articleContainerID instanceof OfferID) {
			OfferID offerID = (OfferID) articleContainerID;
			OfferDAO.sharedInstance().signalOffer(offerID, JbpmConstantsOffer.Customer.NODE_NAME_CUSTOMER_REJECTED, new NullProgressMonitor());
		}
	}

}
