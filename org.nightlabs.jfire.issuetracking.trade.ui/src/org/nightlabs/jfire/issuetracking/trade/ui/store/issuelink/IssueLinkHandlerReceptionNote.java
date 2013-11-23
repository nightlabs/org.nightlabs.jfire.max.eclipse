/**
 *
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

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
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.store.dao.ReceptionNoteDAO;
import org.nightlabs.jfire.store.id.ReceptionNoteID;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.l10n.GlobalDateFormatter;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerReceptionNote
extends AbstractIssueLinkHandler<ReceptionNoteID, ReceptionNote>
{
	@Override
	protected Collection<ReceptionNote> _getLinkedObjects(Set<IssueLink> issueLinks, Set<ReceptionNoteID> linkedObjectIDs, ProgressMonitor monitor) {
		Collection<ReceptionNote> receptionNoteSet = null;
		try {
			receptionNoteSet = ReceptionNoteDAO.sharedInstance().getReceptionNotes(
					linkedObjectIDs,
					new String[] { FetchPlan.DEFAULT },
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return receptionNoteSet;
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, ReceptionNote linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(),
				IssueLinkHandlerReceptionNote.class,
				"LinkedObject").createImage(); //$NON-NLS-1$
	}

	@Override
	public String getLinkedObjectName(IssueLink issueLink, ReceptionNote linkedObject) {
//		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
		return String.format(
				Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink.IssueLinkHandlerReceptionNote.receptionNote.name"), //$NON-NLS-1$
				ArticleContainerUtil.getArticleContainerID(linkedObject),
				GlobalDateFormatter.sharedInstance().formatDate(linkedObject.getCreateDT(), IDateFormatter.FLAGS_DATE_SHORT));
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, ReceptionNoteID linkedObjectID) {
		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
	}

}
