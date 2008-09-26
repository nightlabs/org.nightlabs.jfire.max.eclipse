/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reserve;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author daniel
 *
 */
public class ReserveAction extends ArticleContainerAction {

	/**
	 *
	 */
	public ReserveAction() {
	}

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
		if (articleContainer instanceof Offer)
			return true;

//		if (articleContainer.getArticleCount() > 0)
//			return true;

		return false;
	}

	@Override
	public void run()
	{
		// TODO get quickSearch text from quickSaleEditor
		LegalEntity legalEntity = LegalEntitySearchCreateWizard.open("", true);
		if (legalEntity != null) {
			Offer offer = (Offer) getArticleContainer();
			try {
				offer.setFinalized(Login.getLogin().getUser(new String[]{FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
//				offer.getOrder().setCustomer(legalEntity);
				TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
				tm.assignCustomer((OrderID) JDOHelper.getObjectId(offer.getOrder()), customerID, true, null, 1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
