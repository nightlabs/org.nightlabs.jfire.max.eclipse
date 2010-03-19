/**
 *
 */
package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.overview.offer.action.EditOfferAction;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerOffer
extends AbstractIssueLinkHandler<OfferID, Offer>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, Offer offer) {
		return String.format(
				Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.IssueLinkHandlerOffer.offer.name"), //$NON-NLS-1$
//				linkedObject.getPrimaryKey());
				ArticleContainerUtil.getArticleContainerID(offer),
				DateFormatter.formatDateShort(offer.getFinalizeDT(), false));
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, Offer linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(),
				IssueLinkHandlerOffer.class,
				"LinkedObject").createImage(); //$NON-NLS-1$
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, OfferID linkedObjectID) {
		EditOfferAction editAction = new EditOfferAction();
		editAction.setArticleContainerID(linkedObjectID);
		editAction.run();
	}

	@Override
	protected Collection<Offer> _getLinkedObjects(Set<IssueLink> issueLinks,
			Set<OfferID> linkedObjectIDs, ProgressMonitor monitor) {
		return OfferDAO.sharedInstance().getOffers(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
}
